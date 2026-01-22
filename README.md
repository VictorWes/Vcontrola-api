# VControla - Sistema de Gestão Financeira

## 📋 Sobre o Projeto

Sistema de gestão financeira desenvolvido com **Spring Boot** e **Angular**, permitindo controle completo de finanças pessoais com autenticação JWT e containerização Docker.

## 🚀 Status do Projeto

**✅ EM PRODUÇÃO**

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Security** com autenticação JWT
- **Spring Data JPA**
- **PostgreSQL**
- **Lombok**
- **Bean Validation**

### Frontend
- **Angular**
- Consumindo API REST

### DevOps
- **Docker & Docker Compose**
- **Maven**

## 📦 Funcionalidades

- ✅ Autenticação e Autorização com JWT
- ✅ Gestão de Usuários
- ✅ Controle de Contas Financeiras
- ✅ Gestão de Transações
- ✅ Planejamento Financeiro
- ✅ Controle de Cartões de Crédito
- ✅ Dashboard de Resumo Financeiro

## 🔧 Pré-requisitos

- Java 17+
- Docker e Docker Compose
- Maven
- Node.js e npm (para o frontend Angular)

## 🚀 Como Executar

### 1. Clone o repositório
```bash
git clone <seu-repositorio>
cd vcontrola
```

### 2. Configurar variáveis de ambiente

Crie um arquivo `.env` ou configure a variável de ambiente:
```bash
JWT_SECRET=sua-chave-secreta-aqui
```

Ou edite o `application.properties` para usar o valor padrão (não recomendado para produção).

### 3. Subir o banco de dados com Docker
```bash
docker-compose up -d
```

### 4. Executar a aplicação
```bash
.\mvnw.cmd spring-boot:run
```

Ou compile e execute:
```bash
.\mvnw.cmd clean package -DskipTests
java -jar target/vcontrola-0.0.1-SNAPSHOT.jar
```

### 5. Executar o frontend Angular
```bash
cd frontend
npm install
ng serve
```

Acesse: `http://localhost:4200`

## 🔐 Segurança

- Autenticação via **JWT (JSON Web Token)**
- Senhas criptografadas com **BCrypt**
- CORS configurado para permitir apenas origens confiáveis
- Proteção contra CSRF desabilitada (API Stateless)

## 📊 Banco de Dados

O projeto utiliza **PostgreSQL** rodando em Docker na porta **5430**.

### Configuração padrão:
- **Host:** localhost
- **Porta:** 5430
- **Database:** vcontrola
- **Usuário:** postgres
- **Senha:** postgres

## 📝 Endpoints da API

### Públicos (sem autenticação)
- `POST /usuarios/login` - Login de usuário
- `POST /usuarios` - Cadastro de novo usuário

### Privados (requer JWT)
- `GET/POST/PUT/DELETE /contas` - Gestão de contas
- `GET/POST/PUT/DELETE /transacoes` - Gestão de transações
- `GET/POST /planejamento` - Planejamento financeiro
- `GET /dashboard` - Resumo financeiro

## 🐳 Docker

O projeto possui configuração Docker Compose para subir o banco PostgreSQL:

```bash
docker-compose up -d    # Subir serviços
docker-compose down     # Parar serviços
docker-compose logs     # Ver logs
```

## 🧪 Testes

```bash
.\mvnw.cmd test
```

## 📄 Licença

Este projeto está sob licença MIT.

## 👨‍💻 Autor

Desenvolvido para controle financeiro pessoal.

---

**⚠️ Observações de Segurança:**
- Altere a chave JWT_SECRET em produção
- Configure CORS apenas para domínios confiáveis
- Use HTTPS em produção
- Nunca commite senhas ou chaves no repositório

