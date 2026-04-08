# FinanceBot — Contexto do Projeto

## Visão Geral

Aplicação fullstack para registrar e visualizar finanças pessoais por linguagem natural.
O usuário envia frases como _"gastei 30 mercado"_ e uma IA (Gemini) extrai automaticamente **tipo**, **valor** e **categoria**, salvando no banco.

---

## Arquitetura

```
┌─────────────┐    HTTP REST     ┌──────────────────┐    Gemini API     ┌─────────────┐
│  Frontend   │ ──────────────►  │  Backend         │ ───────────────►  │  IA (Gemini)│
│  React 19   │ ◄────────────── │  Spring Boot 3.2 │ ◄───────────────  │  Flash Lite │
│  Vite       │                  │  Java 21         │                   │             │
└─────────────┘                  └────────┬─────────┘                   └─────────────┘
                                          │
                                          │ PostgreSQL
                                          ▼
                                  Finance Records DB
```

---

## Backend

### Tecnologias
- **Spring Boot 3.2.4** com Java 21
- **Spring Data JPA** + Hibernate
- **PostgreSQL** (banco: `financebot`, usuário: `postgres`, porta: `5432`)
- **RestTemplate** (HTTP client para Gemini)
- **Lombok** apenas na entidade `FinanceRecord`

### Caminho base: `src/main/java/com/financebot/whats/`

### Entity

**`entity/FinanceRecord.java`**
- `id` (Long, auto-generated), `userPhone` (String), `tipo` (String: "gasto" ou "receita")
- `valor` (Double), `categoria` (String), `createdAt` (LocalDateTime)
- Usa Lombok `@Getter @Setter`, `@Entity`, `@Table(name = "finance_records")`

### DTOs (`dto/`)

| DTO | Campos | Uso |
|-----|--------|-----|
| `FinanceMessageDTO` | `message`, `user` | Input do `POST /finance/message` |
| `AiResponseDTO` | `tipo`, `valor`, `categoria` | Response parseado da IA Gemini |
| `FinanceRecordDTO` | `id`, `tipo`, `valor`, `categoria`, `createdAt` | Response do histórico |
| `CategoriaResumoDTO` | `categoria`, `totalGasto`, `totalReceita`, `quantidade` | Response do resumo por categoria |

### Repository

**`repository/FinanceRepository.java`** — todas as queries:

| Método | O que faz |
|--------|-----------|
| `findByUserPhone(user)` | lista tudo de um usuário |
| `sumByUserAndTipo(user, tipo)` | soma gastos ou receitas |
| `findHistorico(user, inicio, fim, categoria, tipo, Pageable)` | paginação com filtros opcionais (`OR :param IS NULL`) |
| `findResumoPorCategoria(user, inicio, fim)` | agrega com `CASE WHEN` — retorna `List<CategoriaResumoDTO>` |
| `findMaiorGasto(user)` | `MAX(valor)` dos gastos |
| `findGastoMes(user, mes, ano)` | soma gastos de um mês específico |
| `countByUser(user)` | total de transações |
| `findByIdAndUser(id, user)` | segura-deleção: valida que transação pertence ao usuário |

**Decisão importante:** `findHistorico` retorna `Page<FinanceRecord>` e o service mapeia para DTO. Não usa projeção `SELECT new DTO(...)` com paginação porque o Hibernate tem bugs conhecidos com contagem automática.

### Services

**`service/AiService.java`**
- Chama Gemini API (`gemini-2.0-flash-lite:generateContent`) com prompt de extração JSON
- Extrai texto do response JSON, remove markdown ````json ... ```, faz parse para `AiResponseDTO`
- Retorna `null` em caso de erro (tratado pelo FinanceService)
- API key injetada via `@Value("${gemini.api.key}")`

**`service/FinanceService.java`**
- `processMessage(message, user)` — recebe texto, chama AI, salva no banco, retorna confirmação textual
- `getResumo(user)` — retorna string formatada com receitas, gastos, saldo, maior gasto, comparativo mensal, total de transações
- `getHistorico(user, dataInicio, dataFim, categoria, tipo, Pageable)` — retorna `Page<FinanceRecordDTO>`
- `getCategoriaResumo(user, dataInicio, dataFim)` — retorna `List<CategoriaResumoDTO>`
- `deletarTransacao(id, user)` — deleta só se pertencer ao usuário

**Decisão importante:** `getResumo` retorna `String` (não DTO) para não quebrar o front existente que usa `response.text()`.

### Controller

**`controller/WhatsAppController.java`** — `@RequestMapping("/finance")`:

| Método | Endpoint | Response | Descrição |
|--------|----------|----------|-----------|
| `POST` | `/finance/message` | `String` | Envia mensagem pra IA e salva |
| `GET` | `/finance/resumo/{user}` | `String` | Resumo textual completo |
| `GET` | `/finance/historico/{user}` | `Page<FinanceRecordDTO>` | Histórico paginado com filtros |
| `GET` | `/finance/categoria-resumo/{user}` | `List<CategoriaResumoDTO>` | Gastos agrupados por categoria |
| `DELETE` | `/finance/transacao/{id}?user={user}` | `ResponseEntity<String>` | Deleta transação (com validação) |

**Endpoints de histórico** aceitam query params opcionais: `dataInicio`, `dataFim`, `categoria`, `tipo`, `page` (default 0), `size` (default 50). Data no formato ISO (`YYYY-MM-DD`).

**Decisão importante:** `@CrossOrigin(origins = "*")` — permite requisições de qualquer origem (apenas dev, não prod). Usuário é passado como query param no DELETE para segurança básica.

### Config

**`application.properties`**
```
server.port=3000
spring.datasource.url=jdbc:postgresql://localhost:5432/financebot
spring.datasource.username=postgres
spring.datasource.password=123123
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
gemini.api.key=<key>
```

**`pom.xml`** — usa `compilerArgs --enable-preview` (recurso preview do Java 21).

---

## Frontend

### Tecnologias
- **React 19** + **Vite 7**
- Zero bibliotecas de UI (CSS puro, sem router)
- Sem biblioteca de gráficos (ainda)

### Caminho base: `src/`

### Serviços

**`services/api.js`**

| Função | Endpoint | Uso |
|--------|----------|-----|
| `sendMessage(message, user)` | POST `/finance/message` | Envia frase natural |
| `getResumo(user)` | GET `/finance/resumo/{user}` | Resumo textual |
| `getHistorico(user, params)` | GET `/finance/historico/{user}?...` | Tabela paginada com filtros |
| `getCategoriaResumo(user, params)` | GET `/finance/categoria-resumo/{user}` | Lista de categorias |
| `deleteTransacao(id, user)` | DELETE `/finance/transacao/{id}?user={user}` | Deletar transação |

`params` aceita: `dataInicio`, `dataFim`, `categoria`, `tipo`, `page`, `size`.

### Componentes

| Componente | O que faz |
|------------|-----------|
| `App.jsx` | Raiz — renderiza `FinanceForm` e `Historico` (via abas) |
| `components/FinanceForm.jsx` | Input de mensagem natural + botão enviar + botão resumo |
| `components/Historico.jsx` | Tabela de transações + filtros + paginação + botão deletar + resumo por categoria |

### Fluxo do usuário

1. Usuario abre o front em `http://localhost:5173`
2. Digita frase tipo `"gastei 30 mercado"` → backend chama Gemini → salva no PostgreSQL → retorna "Anotado! gasto de R$ 30 em mercado"
3. Usuario clica "Ver Resumo" → mostra string com receitas, gastos, saldo, comparativo mensal
4. Usuario clica aba "Histórico" → vê tabela paginada com filtros (data, categoria, tipo) e pode deletar transações
5. Componente Historico carrega automaticamente `getCategoriaResumo` no mount para mostrar gastos agrupados

---

## Como Rodar

### Backend (IntelliJ)
1. PostgreSQL rodando com banco `financebot`
2. `./mvnw spring-boot:run` (porta 3000)

### Frontend (VSCode)
1. `npm install`
2. `npm run dev` (porta 5173)

---

## Decisões Arquiteturais Importantes

### Por que `userPhone IS NULL OR` nas queries?
Permite filtros opcionais com uma única query. Se param não é passado, ignora aquele filtro. Alternative seria Criteria API ou JPQL dinâmico — overkill para 4 filtros.

### Por que não usar DTO projection em `findHistorico`?
Spring Data Page com projeção `SELECT new DTO(...)` falha em gerar a query COUNT automaticamente em algumas versões do Hibernate. Mapear no service é mais seguro.

### Por que `CASE WHEN` em `findResumoPorCategoria`?
Resolve gastos e receitas em uma única query. Sem isso seriam 2 queries separadas + merge manual no Java.

### Por que `findByIdAndUser` antes de deletar?
Sem validação, quem souber o ID de uma transação de outro usuário poderia deletá-la. Validação por `userPhone` garante isolamento básico.

### Por que `getResumo` retorna String e não DTO?
Endpoint já existia e o frontend usa `response.text()`. Mudar pra DTO quebraria o front. Refatorar ambos juntos depois, se necessário.

### Por que não há autenticação?
É um MVP/protótipo. Usuário é passado como string no body/query. Para prod: JWT, session auth, ou OAuth seria necessário.

---

## Limitações Conhecidas

1. **Sem autenticação** — qualquer um pode acessar dados de qualquer usuário sabendo o nome
2. **API key exposta** em `application.properties` — usar variável de ambiente em prod
3. **Sem validação de input** — DTOs não usam `@Valid`/`@NotBlank`
4. **`AiService` retorna `null`** silenciosamente — melhoraria com exceção tipada
5. **Sem webhooks de WhatsApp** — o nome é "WhatsAppController" mas é apenas REST genérico
6. **`@CrossOrigin("*")`** — não seguro para deployment
7. **`--enable-preview`** no compiler — recursos preview do Java podem mudar entre versões
