package br.com.fiap.agendamento.application.usecase.agendamento;

import br.com.fiap.agendamento.application.dto.CriarAgendamentoRequest;
import br.com.fiap.agendamento.application.port.NotificacaoService;
import br.com.fiap.agendamento.application.usecase.disponibilidade.ConsultarDisponibilidadeUseCase;
import br.com.fiap.agendamento.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.agendamento.domain.exception.RegraDeNegocioException;
import br.com.fiap.agendamento.domain.model.Agendamento;
import br.com.fiap.agendamento.domain.model.Servico;
import br.com.fiap.agendamento.domain.model.Usuario;
import br.com.fiap.agendamento.domain.repository.AgendamentoRepository;
import br.com.fiap.agendamento.domain.repository.ProfissionalRepository;
import br.com.fiap.agendamento.domain.repository.ServicoRepository;
import br.com.fiap.agendamento.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Caso de uso central do sistema: criar um agendamento.
 *
 * Estrategia de dupla camada contra double-booking (concorrencia real):
 *  1) Checagem otimista em memoria (existsByProfissionalIdAndDataHora) - resolve o
 *     caso feliz e da feedback rapido ao usuario.
 *  2) Unique constraint no banco (uk_profissional_data_hora, ver migration V1) - garante
 *     a integridade mesmo se duas requisicoes chegarem exatamente ao mesmo tempo;
 *     nesse caso o banco rejeita a segunda escrita e nos traduzimos para RegraDeNegocioException.
 *
 * Alem disso, valida se o horario pedido cai dentro da agenda do profissional
 * (feature 2/3 do desafio) e dispara a notificacao de confirmacao (feature 3).
 */
@Service
@RequiredArgsConstructor
public class CriarAgendamentoUseCase {

    private final AgendamentoRepository agendamentoRepository;
    private final ProfissionalRepository profissionalRepository;
    private final ServicoRepository servicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConsultarDisponibilidadeUseCase consultarDisponibilidadeUseCase;
    private final NotificacaoService notificacaoService;

    @Transactional
    public Agendamento executar(CriarAgendamentoRequest request, UUID clienteId) {
        var profissional = profissionalRepository.findById(request.profissionalId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Profissional nao encontrado: " + request.profissionalId()));

        Servico servico = servicoRepository.findById(request.servicoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Servico nao encontrado: " + request.servicoId()));

        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente nao encontrado: " + clienteId));

        if (!consultarDisponibilidadeUseCase.estaDentroDeAlgumaJanela(request.profissionalId(), request.dataHora())) {
            throw new RegraDeNegocioException(
                    "Horario fora da agenda de disponibilidade do profissional. Consulte /api/profissionais/{id}/disponibilidades");
        }

        boolean conflito = agendamentoRepository.existsByProfissionalIdAndDataHora(
                request.profissionalId(), request.dataHora());
        if (conflito) {
            throw new RegraDeNegocioException(
                    "Profissional ja possui um agendamento neste horario. Escolha outro horario.");
        }

        Agendamento agendamento = Agendamento.builder()
                .clienteId(clienteId)
                .profissionalId(request.profissionalId())
                .servicoId(request.servicoId())
                .estabelecimentoId(servico.getEstabelecimentoId())
                .dataHora(request.dataHora())
                .build();

        Agendamento salvo;
        try {
            salvo = agendamentoRepository.save(agendamento);
        } catch (DataIntegrityViolationException e) {
            // Corrida vencida pelo banco: outra requisicao concorrente confirmou o mesmo horario primeiro.
            throw new RegraDeNegocioException(
                    "Profissional ja possui um agendamento neste horario. Escolha outro horario.");
        }

        notificacaoService.enviarConfirmacao(salvo, cliente.getEmail());
        if (profissional.getEmailContato() != null && !profissional.getEmailContato().isBlank()) {
            notificacaoService.enviarConfirmacao(salvo, profissional.getEmailContato());
        }
        return salvo;
    }
}
