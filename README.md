🚀 VControla - Gestão Financeira Inteligente
Status do Projeto: 🏗️ Em Desenvolvimento (Arquitetura de Domínio e Persistência)

📌 Sobre o Projeto
O VControla é uma solução robusta de controle financeiro pessoal. O diferencial do projeto é a gestão inteligente de fluxo de caixa, permitindo que o usuário visualize o impacto de gastos à vista e parcelados (cartão de crédito) no seu saldo disponível em tempo real.

O projeto segue práticas rigorosas de mercado, como Clean Architecture, Conventional Commits e separação total entre os ecossistemas de Front-end (Angular) e Back-end (Java).

🛠️ Tecnologias e Ferramentas
Linguagem: Java 17

Framework: Spring Boot 3.x

Persistência: Spring Data JPA / Hibernate

Validação: Jakarta Bean Validation (Hibernate Validator)

Banco de Dados: PostgreSQL

Infraestrutura: Docker & Docker Compose

Gerenciador de Dependências: Maven

🏛️ Estrutura de Domínio (Entidades)
A modelagem atual suporta as seguintes funcionalidades críticas:

Usuários: Gerenciamento de perfil com validações de segurança.

Contas: Gestão de múltiplas fontes de recurso (Corrente, Espécie, etc.).

Cartões de Crédito: Controle de limite total, disponível e ciclos de fechamento/vencimento.

Transações: Sistema de lançamentos com suporte a parcelamentos inteligentes (uso de transaction_group_id para vincular parcelas de uma mesma compra).

📅 Roadmap de Desenvolvimento
[x] Modelagem de Entidades (JPA)

[x] Configuração de Infraestrutura Docker

[ ] Implementação de Repositories e Services

[ ] Implementação de DTOs e MapStruct

[ ] Segurança com Spring Security e JWT

[ ] Integração com Front-end Angular
