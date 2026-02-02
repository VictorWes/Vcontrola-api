# VControla - Sistema de Gestão Financeira 💰

> **Status:** 🚀 **EM PRODUÇÃO**

## 🌐 Acesso ao Sistema

**🔗 Aplicação em Produção:** [https://vcontrola.vercel.app/auth/login](https://vcontrola.vercel.app/auth/login)

> ⚠️ **Importante:** A aplicação já está disponível para testes! Acesse o link acima para criar sua conta e começar a usar.

## 📋 Sobre o Projeto

VControla é um sistema completo de gestão financeira desenvolvido para controle de contas, transações, cartões de crédito e planejamento financeiro. O projeto oferece uma API REST robusta integrada com uma aplicação Angular moderna e responsiva, permitindo controle total sobre suas finanças pessoais.

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Data JPA**
- **Spring Security**
- **JWT (JSON Web Token)** - Autenticação e autorização
- **PostgreSQL** - Banco de dados
- **Docker** - Containerização
- **Lombok** - Redução de código boilerplate
- **Bean Validation** - Validação de dados (@NotBlank, @NotNull, etc)
- **Maven** - Gerenciamento de dependências

### Frontend
- **Angular 17+** - Framework frontend
- **TypeScript** - Linguagem principal
- **RxJS** - Programação reativa
- **Angular Router** - Navegação
- **HttpClient** - Comunicação com API
- **Vercel** - Deploy e hospedagem

### DevOps & Deploy
- **Vercel** - Hospedagem do frontend
- **Docker Compose** - Orquestração de containers
- **GitHub** - Controle de versão

## 🔐 Segurança

- ✅ Autenticação via JWT
- ✅ Senhas criptografadas com BCrypt
- ✅ CORS configurado para integração com Angular
- ✅ Endpoints públicos: login e cadastro
- ✅ Endpoints privados protegidos por token
- ✅ Configuração de segurança com Spring Security
- ✅ CSRF desabilitado para API REST
- ✅ SecurityFilter personalizado para validação de tokens
- ✅ Controle de acesso por usuário autenticado

## 🐳 Docker

O projeto utiliza Docker Compose para facilitar o ambiente de desenvolvimento com PostgreSQL containerizado.

## 📦 Pré-requisitos

### Para Desenvolvimento Local

- Java 17+
- Maven 3.6+
- Docker e Docker Compose
- Node.js 18+ (para o frontend)

### Para Testar em Produção

- Apenas um navegador web moderno
- Acesse: [https://vcontrola.vercel.app/auth/login](https://vcontrola.vercel.app/auth/login)

## 🗂️ Estrutura do Projeto

```
vcontrola/
├── src/
│   ├── main/
│   │   ├── java/com/vcontrola/vcontrola/
│   │   │   ├── controller/           # Controladores REST
│   │   │   │   ├── CartaoController.java
│   │   │   │   ├── CompraController.java
│   │   │   │   ├── ContaController.java
│   │   │   │   ├── DashBoardController.java
│   │   │   │   ├── FinanceiroController.java
│   │   │   │   ├── ParcelaController.java
│   │   │   │   ├── TipoContaController.java
│   │   │   │   ├── TransacaoController.java
│   │   │   │   ├── UsuarioController.java
│   │   │   │   ├── request/          # DTOs de requisição
│   │   │   │   └── response/         # DTOs de resposta
│   │   │   ├── entity/               # Entidades JPA
│   │   │   │   ├── Usuario.java
│   │   │   │   ├── Conta.java
│   │   │   │   ├── Transacao.java
│   │   │   │   ├── CartaoCredito.java
│   │   │   │   ├── Compra.java
│   │   │   │   ├── Parcela.java
│   │   │   │   ├── ControleFinanceiro.java
│   │   │   │   ├── ItemPlanejamento.java
│   │   │   │   └── TipoContaUsuario.java
│   │   │   ├── repository/           # Repositórios JPA
│   │   │   ├── service/              # Lógica de negócio
│   │   │   ├── mapper/               # Conversão DTO/Entity
│   │   │   ├── infra/
│   │   │   │   ├── security/         # Configurações de segurança
│   │   │   │   │   ├── SecurityConfigurations.java
│   │   │   │   │   ├── SecurityFilter.java
│   │   │   │   │   └── TokenService.java
│   │   │   │   └── exception/        # Tratamento de exceções
│   │   │   │       ├── GlobalExceptionHandler.java
│   │   │   │       └── RegraDeNegocioException.java
│   │   │   └── enums/                # Enumeradores
│   │   │       ├── TipoConta.java
│   │   │       ├── TipoTransacao.java
│   │   │       ├── StatusTransacaoCartao.java
│   │   │       └── StatusPlanejamento.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/                         # Testes unitários
├── docker-compose.yml
├── Dockerfile
├── pom.xml
└── README.md
```

## 🎯 Funcionalidades Implementadas

### ✅ Autenticação e Autorização
- [x] Cadastro de usuários com validação
- [x] Login com JWT (geração de token)
- [x] Proteção de rotas via SecurityFilter
- [x] Validação de token em cada requisição
- [x] Controle de sessão por usuário
- [x] Logout seguro

### ✅ Gestão de Contas Bancárias
- [x] Criar conta bancária
- [x] Listar contas do usuário
- [x] Editar conta (nome, saldo, tipo)
- [x] Excluir conta (com validação de vínculos)
- [x] Visualizar saldo em tempo real
- [x] Tipos de conta personalizados (TipoContaUsuario)
- [x] Suporte para múltiplas contas por usuário

### ✅ Controle de Transações
- [x] Registrar receitas
- [x] Registrar despesas
- [x] Histórico completo de transações
- [x] Filtros por data/tipo/conta
- [x] Categorização de transações
- [x] Edição de transações existentes
- [x] Exclusão de transações
- [x] Atualização automática de saldo das contas
- [x] Validação de saldo antes de despesas

### ✅ Cartões de Crédito
- [x] Cadastro de cartões (nome, limite, vencimento)
- [x] Listagem de cartões do usuário
- [x] Edição de informações do cartão
- [x] Exclusão de cartões
- [x] Controle de limite disponível
- [x] Visualização de faturas

### ✅ Gestão de Compras no Cartão
- [x] Registrar compras no cartão
- [x] Suporte a compras parceladas
- [x] Listar compras por cartão (com paginação)
- [x] Editar compras
- [x] Excluir compras (com estorno opcional)
- [x] Cálculo automático de parcelas
- [x] Status de compra (PENDENTE/PAGA/CANCELADA)

### ✅ Controle de Parcelas
- [x] Geração automática de parcelas
- [x] Listagem de parcelas por compra
- [x] Pagar parcela (debitando de uma conta)
- [x] Estornar pagamento de parcela
- [x] Visualização de vencimentos
- [x] Status por parcela (PENDENTE/PAGA/CANCELADA)
- [x] Atualização automática do limite do cartão

### ✅ Dashboard e Relatórios
- [x] Resumo financeiro mensal
- [x] Total de receitas do mês
- [x] Total de despesas do mês
- [x] Saldo do mês (receitas - despesas)
- [x] Cálculo automático por período
- [x] Dados em tempo real

### ✅ Planejamento Financeiro (Controle Financeiro Virtual)
- [x] Criar controle financeiro por usuário
- [x] Adicionar saldo virtual
- [x] Criar itens de planejamento (metas/objetivos)
- [x] Editar itens de planejamento
- [x] Excluir itens
- [x] Alternar status (ATIVO/CONCLUIDO)
- [x] Resgate parcial de valores
- [x] Acompanhamento de progresso
- [x] Visualização de resumo financeiro

### ✅ Validações e Tratamento de Erros
- [x] Bean Validation em todas as entidades
- [x] @NotBlank em campos obrigatórios
- [x] @NotNull em campos não nulos
- [x] GlobalExceptionHandler para erros centralizados
- [x] Tratamento de DataIntegrityViolationException
- [x] RegraDeNegocioException personalizada
- [x] Mensagens de erro descritivas
- [x] Validação de vínculos antes de exclusões
- [x] Feedback amigável ao usuário

## 📡 Endpoints da API

### Autenticação
- `POST /usuarios/cadastro` - Cadastro de novo usuário
- `POST /usuarios/login` - Login e geração de token JWT

### Contas
- `GET /contas` - Listar contas do usuário
- `POST /contas` - Criar nova conta
- `PUT /contas/{id}` - Atualizar conta
- `DELETE /contas/{id}` - Excluir conta

### Transações
- `GET /transacoes` - Listar transações
- `POST /transacoes` - Criar transação
- `PUT /transacoes/{id}` - Editar transação
- `DELETE /transacoes/{id}` - Excluir transação

### Cartões de Crédito
- `GET /cartoes` - Listar cartões
- `POST /cartoes` - Criar cartão
- `PUT /cartoes/{id}` - Editar cartão
- `DELETE /cartoes/{id}` - Excluir cartão

### Compras
- `POST /compras` - Registrar compra
- `GET /compras/cartao/{cartaoId}` - Listar compras do cartão
- `PUT /compras/{id}` - Editar compra
- `DELETE /compras/{id}` - Excluir compra (com estorno)

### Parcelas
- `GET /parcelas/compra/{compraId}` - Listar parcelas
- `POST /parcelas/{id}/pagar` - Pagar parcela
- `POST /parcelas/{id}/estornar` - Estornar parcela

### Dashboard
- `GET /dashboard/resumo-mensal` - Resumo financeiro do mês

### Planejamento Financeiro
- `GET /financeiro` - Buscar resumo financeiro
- `POST /financeiro/saldo` - Adicionar saldo virtual
- `POST /financeiro/item` - Criar item de planejamento
- `PUT /financeiro/item/{id}` - Atualizar item
- `DELETE /financeiro/item/{id}` - Excluir item
- `PATCH /financeiro/item/{id}/alternar` - Alternar status
- `POST /financeiro/item/{id}/resgatar` - Resgate parcial

### Tipos de Conta
- `GET /tipos-conta` - Listar tipos de conta do usuário
- `POST /tipos-conta` - Criar tipo personalizado

## 🧪 Testando a Aplicação

### 1. Acesse a aplicação em produção
[https://vcontrola.vercel.app/auth/login](https://vcontrola.vercel.app/auth/login)

### 2. Crie sua conta
- Clique em "Cadastrar"
- Preencha seus dados (nome, email, senha)
- Faça login com suas credenciais

### 3. Explore as funcionalidades
✅ **Contas:** Crie suas contas bancárias (corrente, poupança, etc)
✅ **Transações:** Registre suas receitas e despesas
✅ **Cartões:** Cadastre seus cartões de crédito
✅ **Compras:** Adicione compras parceladas nos cartões
✅ **Parcelas:** Acompanhe e pague suas parcelas
✅ **Dashboard:** Visualize o resumo mensal das suas finanças
✅ **Planejamento:** Crie metas e objetivos financeiros

## 🔧 Executando Localmente

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/vcontrola.git
cd vcontrola

# Inicie o banco de dados com Docker
docker-compose up -d

# Execute a aplicação
./mvnw spring-boot:run

# A API estará disponível em http://localhost:8080
```

## 🚨 Regras de Negócio Implementadas

1. **Contas:** Não é possível excluir uma conta que possui transações, cartões ou planejamentos vinculados
2. **Transações:** Ao criar uma despesa, o saldo da conta é validado e atualizado
3. **Cartões:** O limite disponível é calculado automaticamente baseado nas compras
4. **Parcelas:** Ao pagar uma parcela, o valor é debitado da conta selecionada
5. **Estorno:** Ao estornar uma parcela, o valor retorna para a conta
6. **Planejamento:** O saldo virtual é gerenciado separadamente do saldo real das contas
7. **Autenticação:** Todos os endpoints (exceto login/cadastro) requerem token JWT válido

## 📊 Diferenciais do Projeto

✨ **Separação de Saldo Real e Virtual** - Controle financeiro independente para planejamento
✨ **Paginação** - Listagem eficiente de compras com Pageable
✨ **Tratamento de Exceções Centralizado** - GlobalExceptionHandler com mensagens amigáveis
✨ **Validações Robustas** - Bean Validation em todas as entradas
✨ **Segurança Avançada** - JWT + Spring Security + CORS configurado
✨ **Estorno Inteligente** - Devolução automática de valores ao excluir/estornar
✨ **Dashboard em Tempo Real** - Cálculos automáticos de receitas e despesas

## 📄 Licença

Este projeto está em desenvolvimento ativo.

## 🤝 Contribuindo

Projeto em desenvolvimento ativo. Sugestões e melhorias são bem-vindas!

---

**VControla** - Seu controle financeiro completo e inteligente 💼

🚀 **[Teste agora em produção!](https://vcontrola.vercel.app/auth/login)**
