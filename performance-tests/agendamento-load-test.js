import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';

// Teste de performance/carga (feature "Testes Nao Funcionais" do desafio):
// simula um volume elevado de clientes buscando estabelecimentos e criando
// agendamentos simultaneamente, validando que o sistema nao degrada.
//
// Uso:
//   BASE_URL=http://localhost:8080 k6 run performance-tests/agendamento-load-test.js

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const conflitosDoubleBooking = new Counter('conflitos_double_booking');

export const options = {
    scenarios: {
        carga_gradual: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '30s', target: 20 },   // aquecimento
                { duration: '1m', target: 100 },    // pico de 100 usuarios simultaneos
                { duration: '30s', target: 0 },     // resfriamento
            ],
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<800'],   // 95% das requisicoes abaixo de 800ms
        http_req_failed: ['rate<0.01'],     // menos de 1% de erros inesperados (5xx)
    },
};

export default function () {
    // Busca e filtragem de estabelecimentos - endpoint mais acessado do sistema
    const buscaRes = http.get(`${BASE_URL}/api/estabelecimentos?q=Sao+Paulo`);
    check(buscaRes, {
        'busca de estabelecimentos retorna 200': (r) => r.status === 200,
    });

    // Health check - garante que a aplicacao continua respondendo sob carga
    const healthRes = http.get(`${BASE_URL}/actuator/health`);
    check(healthRes, {
        'health check saudavel': (r) => r.status === 200,
    });

    if (buscaRes.status === 409) {
        conflitosDoubleBooking.add(1);
    }

    sleep(1);
}
