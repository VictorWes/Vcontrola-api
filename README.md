# ☕ VControla - Backend API

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![License](https://img.shields.io/badge/license-MIT-green?style=for-the-badge)

> **Status:** 🚀 Em desenvolvimento (API RESTful em fase de refinamento)

O **VControla API** é o motor do ecossistema VControla. Construído com **Java 17** e **Spring Boot 3**, ele gerencia toda a lógica de negócio financeira, garantindo a integridade das transações, segurança dos dados do usuário e persistência eficiente em banco de dados relacional.

---

## 🏗️ Arquitetura e Design Patterns

O projeto segue uma arquitetura em camadas bem definida para garantir desacoplamento e facilidade de testes:

* **Controllers:** Endpoints REST que recebem as requisições, validam DTOs e retornam Responses padronizados.
* **Services:** O coração da aplicação. Contém as regras de negócio (ex: cálculo de saldo, estornos, validações de propriedade).
* **Repositories:** Camada de acesso a dados usando **Spring Data JPA**.
* **DTOs (Java Records):** Uso de `record` para transferência de dados imutáveis e performáticos entre Front e Back.
* **Mappers:** Componentes dedicados para conversão entre Entity e DTO.

---

## ⚡ Funcionalidades de Destaque

### 🔐 Segurança e Autenticação
- Autenticação Stateless via **JWT (JSON Web Token)**.
- Integração com **Spring Security** para proteção de rotas.
- Criptografia de senhas com **BCrypt**.

### 💸 Gestão Financeira Inteligente (ACID)
- **Atualização Atômica de Saldos:** Ao criar uma transação (Receita/Despesa), o saldo da conta vinculada é atualizado automaticamente.
- **Lógica de Estorno:** Ao editar ou excluir uma transação, o sistema automaticamente reverte o valor no saldo da conta antes de aplicar a nova alteração, garantindo consistência contábil.
- **Paginação e Filtros:** Endpoints otimizados com `Pageable` e filtros dinâmicos via JPA Specifications ou Query Methods.

### 💳 Organização
- **Carteiras vs. Contas:** Estrutura hierárquica onde Contas (Nubank, Santander) pertencem a Carteiras/Objetivos (Reserva, Viagem).

---

## 🛠️ Stack Tecnológica

* **Linguagem:** Java 17+
* **Framework:** Spring Boot 3.x
* **Banco de Dados:** PostgreSQL
* **ORM:** Hibernate / Spring Data JPA
* **Migrations:** Flyway (Gerenciamento de versão do banco)
* **Boilerplate:** Lombok
* **Containerização:** Docker & Docker Compose

---
🛣️ Roadmap
[x] CRUD de Usuários e Autenticação

[x] CRUD de Contas e Atualização de Saldo

[x] CRUD de Transações com Lógica de Estorno

[ ] Implementação de Cartão de Crédito (Faturas e Limites)

[ ] Relatórios e Agregações para Dashboard

[ ] CI/CD Pipeline (GitHub Actions -> Railway)
