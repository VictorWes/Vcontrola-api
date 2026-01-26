# VControla - Sistema de Gestão Financeira 💰

> **Status:** 🚀 **EM PRODUÇÃO**

## 🌐 Acesso ao Sistema

**🔗 Aplicação em Produção:** [https://vcontrola.vercel.app/auth/login](https://vcontrola.vercel.app/auth/login)

> ⚠️ **Importante:** A aplicação já está disponível para testes! Acesse o link acima para criar sua conta e começar a usar.

## 📋 Sobre o Projeto

VControla é um sistema completo de gestão financeira desenvolvido para controle de contas, transações e cartões de crédito. O projeto oferece uma API REST robusta integrada com uma aplicação Angular moderna e responsiva.

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

## 🐳 Docker

O projeto utiliza Docker Compose para facilitar o ambiente de desenvolvimento:


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
│   │   │   │   ├── request/          # DTOs de requisição
│   │   │   │   └── response/         # DTOs de resposta
│   │   │   ├── entity/               # Entidades JPA
│   │   │   │   ├── Usuario.java
│   │   │   │   ├── Conta.java
│   │   │   │   ├── Transacao.java
│   │   │   │   ├── CartaoCredito.java
│   │   │   │   ├── ControleFinanceiro.java
│   │   │   │   ├── ItemPlanejamento.java
│   │   │   │   └── TipoContaUsuario.java
│   │   │   ├── repository/           # Repositórios JPA
│   │   │   ├── service/              # Lógica de negócio
│   │   │   ├── mapper/               # Conversão DTO/Entity
│   │   │   ├── infra/security/       # Configurações de segurança
│   │   │   │   ├── SecurityConfigurations.java
│   │   │   │   ├── SecurityFilter.java
│   │   │   │   └── TokenService.java
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
- [x] Cadastro de usuários
- [x] Login com JWT
- [x] Proteção de rotas
- [x] Renovação de token
- [x] Logout

### ✅ Gestão de Contas
- [x] Criar conta bancária
- [x] Listar contas
- [x] Editar conta
- [x] Excluir conta
- [x] Visualizar saldo
- [x] Tipos de conta personalizados

### ✅ Controle de Transações
- [x] Registrar receitas
- [x] Registrar despesas
- [x] Histórico de transações
- [x] Filtros por data/tipo
- [x] Categorização
- [x] Edição de transações
- [x] Exclusão de transações

### ✅ Cartões de Crédito
- [] Cadastro de cartões
- [] Acompanhamento de faturas
- [] Status de transações do cartão
- [] Controle de limite

### ✅ Dashboard e Relatórios
- [] Resumo financeiro
- [] Gráficos de receitas e despesas
- [] Saldo consolidado
- [] Análise por período

### ✅ Planejamento Financeiro
- [] Criar metas financeiras
- [] Acompanhar progresso
- [] Controle de orçamento
- [] Status de planejamento

### ✅ Validações
- [x] Validação de dados com Bean Validation
- [x] @NotBlank em campos obrigatórios
- [x] @NotNull em campos não nulos
- [x] Mensagens de erro personalizadas
- [x] Tratamento de exceções


## 🧪 Testando a Aplicação

### 1. Acesse a aplicação em produção
[https://vcontrola.vercel.app/auth/login](https://vcontrola.vercel.app/auth/login)

### 2. Crie sua conta
- Clique em "Cadastrar"
- Preencha seus dados
- Faça login

### 3. Explore as funcionalidades
- Crie suas contas bancárias
- Registre transações
- Visualize o dashboard
- Configure seu planejamento financeiro

## 📊 Roadmap

### Em Desenvolvimento
- [ ] Notificações por email
- [ ] Backup automático
- [ ] Modo escuro

### Futuras Implementações
- [ ] Relatórios em PDF

## 📄 Licença

Este projeto está em desenvolvimento ativo.

## 🤝 Contribuindo

Projeto em desenvolvimento ativo. Sugestões e melhorias são bem-vindas!


**VControla** - Seu controle financeiro simplificado 💼

🚀 **[Teste agora em produção!](https://vcontrola.vercel.app/auth/login)**
