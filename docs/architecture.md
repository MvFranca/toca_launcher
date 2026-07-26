# 🦊 Arquitetura do Projeto — TOCA Launcher

> Este documento descreve a arquitetura do **TOCA Launcher**, o aplicativo Android voltado para crianças que compõe a **Plataforma TOCA**.
>
> O Launcher é parte de um ecossistema maior que inclui um Backend centralizado e um aplicativo para os pais. Este documento descreve **exclusivamente** a arquitetura do Launcher. O Backend e o App dos Pais são mencionados apenas como contexto de integração.

O objetivo desta arquitetura é permitir que o projeto cresça durante anos mantendo:

* Baixo acoplamento
* Alta coesão
* Fácil manutenção
* Escalabilidade
* Testabilidade
* Separação clara de responsabilidades

---

# Plataforma TOCA

O TOCA Launcher é um dos componentes da Plataforma TOCA, uma solução completa formada por:

```text
              Plataforma TOCA

          Backend (Spring Boot)

                 REST API

        ┌──────────┴──────────┐

TOCA Launcher          TOCA Pais
(Android)              (Android)
```

* **TOCA Launcher** — responsável pela experiência da criança: launcher, gamificação, aprendizado e controle de tempo;
* **TOCA Pais** — responsável pelas configurações parentais: bloqueios, missões, limites de tempo e relatórios;
* **Backend** — centraliza regras, persistência definitiva e sincronização entre os dois aplicativos.

Ambos os aplicativos consomem a mesma API REST. O Launcher nunca se comunica diretamente com o App dos Pais. Toda troca de informações ocorre através do Backend.

Este documento não detalha a arquitetura interna do Backend nem do App dos Pais.

---

# Filosofia

A Toca não é apenas um aplicativo Android.

Ela possui três grandes responsabilidades:

* Um **Launcher Android**
* Uma **plataforma de aprendizado**
* Um **jogo gamificado**

Por isso, a arquitetura precisa refletir essas responsabilidades desde o início.

---

# Arquitetura Geral

```text
app
│
├── core
├── engine
├── feature
├── services
└── build-logic
```

Cada módulo possui uma responsabilidade única.

---

# Offline First

O Launcher foi projetado para funcionar **integralmente sem conexão com a internet**.

A internet é utilizada exclusivamente para sincronização de dados. Toda a experiência principal — launcher, missões, gamificação, controles de tempo, perfil e configurações — permanece disponível independentemente da conectividade.

Essa decisão garante que a criança nunca tenha sua experiência interrompida por instabilidade de rede.

| Funcionalidade        | Offline |
|-----------------------|---------|
| Launcher              | ✅      |
| Missões               | ✅      |
| Apps instalados       | ✅      |
| Apps bloqueados       | ✅      |
| Tempo restante        | ✅      |
| XP                    | ✅      |
| Nível                 | ✅      |
| Medalhas              | ✅      |
| Avatar                | ✅      |
| Perfil                | ✅      |
| Configurações         | ✅      |
| Recompensas pendentes | ✅      |

### Fluxo Offline First

```text
Backend

↓

Primeira sincronização

↓

Room Database

↓

Launcher funciona offline

↓

Usuário utiliza normalmente

↓

Internet disponível

↓

WorkManager

↓

Backend atualizado
```

---

# Fonte da Verdade

A fonte da verdade varia de acordo com o contexto de uso.

**Durante o uso da criança:**

O **Room Database** é a fonte da verdade. Toda leitura de dados ocorre localmente, garantindo performance e disponibilidade offline.

**Durante sincronizações:**

O **Backend** torna-se a fonte da verdade para configurações e regras parentais.

### Política por domínio

| Domínio                   | Fonte da Verdade |
|---------------------------|-----------------|
| Bloqueios de aplicativos  | Backend vence   |
| Tempo permitido           | Backend vence   |
| Missões criadas pelos pais| Backend vence   |
| Configurações parentais   | Backend vence   |
| XP acumulado              | Launcher envia  |
| Progresso de missões      | Launcher envia  |
| Medalhas conquistadas     | Launcher envia  |
| Tempo de uso              | Launcher envia  |
| Conclusão de missões      | Launcher envia  |

---

# Estratégia de Sincronização

## Quando sincronizar

A sincronização ocorre nos seguintes momentos:

* Abertura do Launcher
* Login do usuário
* Internet restabelecida após perda de conectividade
* WorkManager periódico em background
* Sincronização manual acionada pelo usuário
* Retorno do aplicativo ao foreground

## Política de conflitos

**Backend vence:**

* Configurações de bloqueio
* Apps bloqueados
* Tempo permitido
* Missões criadas pelos pais

**Launcher envia:**

* XP gerado
* Progresso nas atividades
* Medalhas conquistadas
* Tempo utilizado por aplicativo
* Missões concluídas

Em caso de conflito de dados enviados pelo Launcher, a estratégia adotada é **last-write-wins** com timestamp, garantindo que a atualização mais recente seja persistida.

---

# app

Responsável apenas por iniciar a aplicação.

Contém:

* MainActivity
* Application
* Navegação inicial
* Configuração do Hilt
* Tema global

Nunca deve conter regras de negócio.

---

# core

Tudo que pode ser reutilizado por qualquer feature.

```text
core
├── analytics
├── common
├── database
├── datastore
├── designsystem
├── device
├── education
├── gamification
├── launcher
├── model
├── navigation
├── network
├── notification
├── permissions
├── security
├── testing
├── ui
└── utils
```

---

## core/designsystem

Nosso Design System.

Responsável por:

* Colors
* Typography
* Icons
* Components
* Theme
* Spacing
* Shapes

Nenhuma feature cria seus próprios componentes visuais.

Tudo deve nascer aqui.

---

## core/ui

Componentes reutilizáveis.

Exemplos:

```text
Button
Card
FoxAvatar
ProgressCard
MissionCard
TopBar
BottomBar
Dialogs
ProgressIndicator
```

---

## core/model

Apenas modelos.

Exemplo:

```kotlin
User
Child
Parent
Mission
Reward
Achievement
Question
InstalledApp
Device
```

Sem lógica.

---

## core/network

Módulo responsável por toda comunicação com a API REST do Backend.

```text
network
├── api            — interfaces Retrofit por domínio
├── authentication — gerenciamento de tokens e refresh
├── interceptors   — logging, autenticação e headers
├── serializers    — adaptadores de serialização/deserialização
├── retry          — política de retry em falhas transitórias
├── sync           — contratos utilizados pela camada de sincronização
└── websocket      — suporte futuro a comunicação em tempo real
```

**api** — define as interfaces Retrofit para cada domínio da plataforma (perfil, missões, gamificação, configurações).

**authentication** — gerencia o ciclo de vida do JWT: armazenamento seguro, refresh automático e logout.

**interceptors** — injetam o token de autenticação em todas as requisições, realizam logging em modo de depuração e adicionam headers necessários.

**serializers** — adaptam os DTOs recebidos da API para os modelos de domínio utilizados pelo Launcher.

**retry** — implementa política de reenvio automático para falhas de rede transitórias com backoff exponencial.

**sync** — expõe contratos utilizados pelos workers de sincronização.

**websocket** — reservado para suporte futuro a eventos em tempo real (notificações instantâneas, atualizações de missões).

---

## core/database

Responsável pelo Room.

Contém:

* Entities
* DAO
* Database

---

## core/datastore

Responsável por preferências.

Exemplo:

* Tema
* Token
* Idioma
* Configurações
* Primeiro acesso

---

## core/navigation

Centraliza toda navegação do aplicativo.

Nenhuma feature conhece rotas diretamente.

---

## core/permissions

Todo gerenciamento de permissões.

Exemplos:

* Accessibility
* Usage Stats
* Notification
* Overlay
* Device Owner
* Battery Optimization

---

## core/security

Tudo relacionado à segurança.

Exemplo:

* JWT
* Android Keystore
* Biometria
* Criptografia

---

## core/device

Abstrações do Android.

Exemplo:

```text
Installed Apps

Battery

Storage

Connectivity

Manufacturer

Screen

Memory

Device Info
```

---

## core/launcher

Abstrações do Launcher.

Nenhuma feature acessa PackageManager diretamente.

Exemplo:

```text
LauncherRepository

InstalledAppsRepository

AppLauncher

ShortcutManager

WidgetManager
```

---

## core/gamification

Modelos e contratos da gamificação.

---

## core/education

Modelos relacionados ao aprendizado.

---

## core/analytics

Contratos e eventos de analytics utilizados pelo módulo `analytics-engine`.

---

# engine

Os engines são o coração da aplicação.

Toda regra de negócio fica aqui.

```text
engine
├── launcher-engine
├── unlock-engine
├── gamification-engine
├── education-engine
├── rewards-engine
├── personalization-engine
└── analytics-engine
```

Esses módulos NÃO conhecem:

* Compose
* Activities
* Fragments
* Navigation

Eles apenas recebem dados e retornam resultados.

---

## launcher-engine

Responsável por toda a lógica de execução e controle do Launcher.

* Home e navegação principal
* Barra inferior e atalhos
* Avatar da criança na interface
* Controle de navegação entre telas
* Integração com missões ativas
* Exibição e controle do tempo restante
* Estado global da UI do Launcher
* XP e progresso visível na home
* Evolução e nível da criança
* Perfil ativo da criança
* Abertura de aplicativos
* Bloqueio de aplicativos
* Verificação de permissões
* Liberação de acesso baseada em regras

---

## unlock-engine

Toda lógica de desbloqueio.

Exemplo:

```
Matemática concluída

↓

10 pontos

↓

desbloquear YouTube

↓

30 minutos
```

---

## gamification-engine

Concentra toda a lógica de gamificação do Launcher.

* XP — cálculo e acúmulo de experiência
* Levels — progressão de nível da criança
* Daily Missions — missões do dia
* Weekly Missions — missões da semana
* Achievements — conquistas desbloqueáveis
* Inventory — gerenciamento de itens do inventário
* Coins — moeda interna do jogo
* Unlockables — itens que podem ser desbloqueados
* Reward System — distribuição e validação de recompensas
* Progress Tracking — rastreamento de progresso em tempo real
* Streak — controle de sequências diárias

Nenhuma feature calcula XP ou valida missões diretamente. Toda essa responsabilidade permanece concentrada neste engine.

---

## education-engine

Toda inteligência do aprendizado.

* Lessons — estrutura e sequenciamento de lições
* Quizzes — perguntas, respostas e validação
* Adaptive Learning — adaptação de conteúdo ao perfil da criança
* Difficulty — controle dinâmico de dificuldade
* Recommendation — recomendação de próximas atividades
* Learning Progress — rastreamento do progresso educacional

Toda lógica educacional permanece isolada neste módulo. Nenhuma feature toma decisões pedagógicas diretamente.

---

## rewards-engine

Calcula recompensas.

---

## personalization-engine

Gerencia toda evolução visual e personalização da Toca.

* Avatar — criação e evolução do avatar da criança
* Inventário — itens cosméticos disponíveis
* Cosméticos — roupas, acessórios e aparência do personagem
* Decoração da Toca — móveis, plantas, objetos e ambientação
* Evolução visual — progressão da aparência conforme o nível
* Personagens — personagens desbloqueáveis
* Itens desbloqueáveis — recompensas visuais obtidas por progresso

---

# feature

Cada funcionalidade vive isoladamente.

```text
feature
├── home
├── explore
├── learn
├── missions
├── achievements
├── rewards
├── inventory
├── decorations
├── profile
├── parents
├── onboarding
└── settings
```

Cada feature possui sua própria arquitetura.

Exemplo:

```text
feature/home

presentation/
domain/
data/
di/
```

---

## presentation

Interface.

Contém:

```text
Screen

ViewModel

UiState

UiAction

UiEvent

Components
```

---

## domain

Casos de uso.

Interfaces.

Regras específicas da feature.

---

## data

Implementações.

Repositories.

Data Sources.

---

## di

Injeção de dependência da feature.

---

## Responsabilidades por Feature

### feature/home

* Exibição do progresso atual da criança
* Missão ativa em destaque
* Avatar animado da criança
* Atalhos para aplicativos favoritos
* Recomendações de atividades

### feature/explore

* Descoberta de novos conteúdos e atividades
* Desafios disponíveis
* Navegação por categorias

### feature/learn

* Atividades educacionais
* Quizzes interativos
* Trilhas de aprendizado

### feature/missions

* Listagem de missões ativas
* Progresso por missão
* Recompensas disponíveis ao concluir

### feature/profile

* Visualização e edição do avatar
* Inventário de itens cosméticos
* Cosméticos equipados
* Evolução e nível atual

---

# services

Componentes Android que vivem fora da interface.

```text
services
├── accessibility
├── launcher
├── notifications
├── sync
├── usage
└── boot
```

---

## accessibility

AccessibilityService.

Detecta aplicativos abertos.

Executa regras.

---

## launcher

Responsável pelo comportamento do Launcher.

---

## usage

Monitora tempo de uso.

---

## notifications

Push.

Lembretes.

Missões.

---

## sync

Módulo responsável pela sincronização em background entre o Launcher e o Backend.

```text
Sync Engine

├── Connectivity Monitor   — detecta mudanças de conectividade e dispara sincronizações
├── Sync Worker            — worker do WorkManager responsável pela execução da sincronização
├── Upload Manager         — envia dados produzidos pelo Launcher ao Backend (XP, progresso, tempo)
├── Download Manager       — baixa configurações e atualizações do Backend para o Room
├── Conflict Resolver      — resolve conflitos entre dados locais e remotos conforme a política definida
└── Retry Policy           — reencaminha operações falhas com backoff exponencial
```

**Connectivity Monitor** — observa o estado da rede em tempo real. Ao detectar conexão disponível, notifica o `Sync Worker` para iniciar a sincronização pendente.

**Sync Worker** — implementado com WorkManager. Garante execução mesmo que o aplicativo esteja em background. Pode ser acionado periodicamente, por mudança de conectividade ou manualmente.

**Upload Manager** — responsável por coletar e enviar ao Backend todos os dados gerados pelo uso da criança: XP, progresso em missões, medalhas conquistadas e tempo de uso por aplicativo.

**Download Manager** — responsável por buscar no Backend as configurações mais recentes: bloqueios, tempo permitido, missões criadas pelos pais e atualizações de perfil.

**Conflict Resolver** — aplica a política de conflitos definida na seção "Fonte da Verdade". Backend vence para configurações; Launcher vence para dados de uso.

**Retry Policy** — gerencia tentativas automáticas de reenvio para operações que falharam por instabilidade de rede, utilizando backoff exponencial com limite máximo de tentativas.

---

## boot

BootReceiver.

Inicialização automática.

---

# build-logic

Convenções do Gradle.

Plugins.

Version Catalog.

Configurações compartilhadas.

---

# Integração com a Plataforma

O Launcher nunca se comunica diretamente com o App dos Pais. Toda comunicação entre os dois aplicativos ocorre exclusivamente através do Backend.

```text
Launcher

↓

REST API

↓

Backend

↓

PostgreSQL

↑

App dos Pais
```

O App dos Pais configura bloqueios, define missões e ajusta limites de tempo. O Backend persiste essas configurações. O Launcher as recebe na próxima sincronização e aplica localmente via Room.

Da mesma forma, o Launcher envia ao Backend os dados produzidos pela criança. O App dos Pais os consome para exibir relatórios e acompanhamento.

---

# Fluxo de Dependências

```text
Feature

↓

Engine

↓

Core

↓

Android
```

Nunca o contrário.

As Features nunca devem depender umas das outras.

Toda comunicação acontece através de interfaces.

---

# Princípios

## Single Responsibility

Cada módulo possui apenas uma responsabilidade.

---

## Feature First

Cada funcionalidade vive isoladamente.

---

## Clean Architecture

A UI nunca conhece regras de negócio.

---

## Dependency Inversion

Toda dependência importante deve ser uma interface.

---

## Testabilidade

Engines devem ser totalmente testáveis sem Android.

---

## Reutilização

Tudo reutilizável deve morar em Core.

---

# Decisões Arquiteturais

## Offline First

O Launcher nunca depende da internet para funcionar. Toda a experiência da criança é servida localmente pelo Room Database. A sincronização ocorre de forma transparente em background.

---

## Backend como sincronizador

O Backend não é a fonte da verdade durante o uso. Ele é o sincronizador. A persistência definitiva ocorre no Backend, mas o Launcher opera de forma completamente autônoma durante a sessão da criança.

---

## Room como cache principal

Durante o uso, o Room é a principal fonte de dados. Todas as queries da UI leem do banco local, garantindo performance consistente independentemente da conectividade.

---

## WorkManager

Toda sincronização ocorre em background via WorkManager. Isso garante que a sincronização seja executada mesmo que o aplicativo seja fechado, respeitando as restrições de bateria e rede definidas pelo sistema.

---

## Engines

Toda regra de negócio permanece concentrada nas Engines. Features nunca calculam XP, validam missões ou tomam decisões de desbloqueio diretamente. Isso garante que as regras sejam reutilizáveis, testáveis e isoladas da interface.

---

## MVVM

ViewModels não possuem regra de negócio. Eles apenas observam estados expostos pelas Engines e pelos UseCases, repassando-os para a UI. Toda lógica que não é de apresentação pertence ao domain ou ao engine correspondente.

---

## Feature First

Cada Feature possui responsabilidades bem definidas e vive isolada das demais. A comunicação entre features, quando necessária, ocorre através de contratos definidos no core ou nos engines.

---

## Clean Architecture

A UI nunca acessa diretamente a camada de dados. O fluxo sempre percorre: Screen → ViewModel → UseCase → Repository → DataSource. Isso garante que cada camada possa ser substituída ou testada de forma independente.

---

# Analytics

Eventos rastreados pelo Launcher.

| Evento               | Descrição                                      |
|----------------------|------------------------------------------------|
| `mission_started`    | Criança iniciou uma missão                     |
| `mission_completed`  | Criança concluiu uma missão                    |
| `reward_claimed`     | Recompensa resgatada                           |
| `lesson_completed`   | Lição educacional concluída                    |
| `app_blocked`        | Aplicativo bloqueado pelo sistema              |
| `app_unlocked`       | Aplicativo desbloqueado após conclusão         |
| `level_up`           | Criança subiu de nível                         |
| `daily_login`        | Primeiro acesso do dia                         |
| `time_earned`        | Tempo de tela conquistado                      |
| `time_used`          | Tempo de uso de aplicativo registrado          |
| `avatar_customized`  | Avatar da criança personalizado                |

---

# Tecnologias

## Android — TOCA Launcher

* Kotlin
* Jetpack Compose
* Material 3
* Navigation Compose
* Room
* Retrofit
* OkHttp
* Hilt
* Coroutines
* Flow
* DataStore
* WorkManager
* Coil
* Lottie
* Firebase Analytics *(futuro)*

## Backend — contexto

O Backend é desenvolvido em Kotlin com Spring Boot, utilizando PostgreSQL como banco de dados relacional e autenticação via JWT. Este documento não detalha sua arquitetura interna.

---

# Referências

## Arquitetura Android

https://developer.android.com/topic/architecture

---

## Modularização

https://developer.android.com/topic/modularization

---

## Recomendações Oficiais

https://developer.android.com/topic/architecture/recommendations

---

## Compose

https://developer.android.com/jetpack/compose

---

## Now in Android

https://github.com/android/nowinandroid

---

# Objetivo

Criar uma base sólida para que o **TOCA Launcher** possa evoluir de um launcher educativo para um componente central de um ecossistema completo de aprendizagem infantil, mantendo uma arquitetura limpa, escalável e preparada para novos módulos, funcionalidades e integrações com a Plataforma TOCA.
