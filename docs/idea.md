# 🦊 TOCA

> **Transformando tempo de tela em tempo de aprendizado.**

## Visão Geral

A **Toca** é um launcher educativo para Android voltado para crianças de aproximadamente 6 a 10 anos.

O launcher substitui completamente a tela inicial do celular por um ambiente gamificado onde a criança precisa realizar atividades educativas para conquistar recompensas e desbloquear o acesso aos demais aplicativos do dispositivo.

O objetivo **não é restringir o uso do celular**, mas transformar o celular em um ambiente de aprendizado, incentivando hábitos saudáveis e promovendo autonomia.

---

# Propósito

Acreditamos que crianças não precisam ser afastadas da tecnologia.

Elas precisam aprender a utilizá-la de forma saudável.

Assim como filhotes de raposa vivem protegidos em uma toca antes de explorar o mundo, a criança utiliza a **Toca** para aprender, brincar e desenvolver habilidades antes de acessar livremente o restante do celular.

---

# Conceito da Marca

"Toca" representa um lugar seguro.

Não significa bloqueio.

Não significa controle.

Representa:

- segurança
- acolhimento
- desenvolvimento
- crescimento
- curiosidade
- descoberta

Toda comunicação da marca deve transmitir esses valores.

Nunca utilizar linguagem que faça parecer um aplicativo de punição ou controle parental.

---

# Público-alvo

## Crianças

- 6 a 10 anos
- Ensino Fundamental
- Gostam de jogos
- Já utilizam smartphones

## Pais

- Buscam reduzir o consumo passivo de telas
- Desejam incentivar aprendizado
- Querem acompanhar a evolução dos filhos
- Não querem entrar em conflito diariamente pelo uso do celular

---

# O Problema

Hoje as crianças utilizam o celular principalmente para consumo passivo.

Exemplos:

- vídeos
- redes sociais
- jogos repetitivos

Os pais acabam utilizando bloqueadores ou aplicativos de controle parental, que normalmente geram frustração e conflitos.

A Toca propõe uma abordagem diferente:

> O acesso ao celular é conquistado através do aprendizado.

---

# A Solução

A Toca substitui a Home do Android.

Toda vez que a criança desbloqueia o celular, ela entra na Toca.

Lá encontra:

- Missões
- Jogos educativos
- Sistema de pontos
- Recompensas
- Conquistas
- Mascote
- Evolução da toca

Ao concluir atividades educativas, a criança recebe pontos que podem desbloquear tempo de uso para aplicativos externos.

---

# Jornada da Criança

```text
Entra na Toca

↓

Recebe uma missão

↓

Joga

↓

Aprende

↓

Ganha pontos

↓

Desbloqueia recompensas

↓

Libera aplicativos

↓

Explora o mundo digital
```

---

# Conceito Visual

A Toca deve parecer um pequeno mundo vivo.

Inspirado em:

- Duolingo
- Headspace
- Animal Crossing
- Khan Academy Kids

Não deve parecer um launcher tradicional.

---

# Mascote

A mascote é uma raposa.

Ela representa:

- inteligência
- curiosidade
- amizade
- incentivo

Ela nunca deve agir como autoridade.

Ela é uma companheira.

A raposa possui dois filhotes, reforçando o conceito de família, cuidado e aprendizado.

---

# Universo da Toca

O launcher representa o interior de uma toca.

Ela evolui conforme a criança progride.

No início:

- ambiente simples

Depois:

- móveis
- plantas
- estantes
- quadros
- medalhas
- decoração

Tudo conquistado através do aprendizado.

---

# Gamificação

A gamificação é um dos pilares do produto.

Elementos:

- XP
- Níveis
- Conquistas
- Medalhas
- Sequências
- Missões diárias
- Recompensas
- Inventário
- Personalização

---

# Recompensas

A criança pode desbloquear:

- tempo de uso do celular
- decoração da toca
- roupas para a raposa
- acessórios
- itens especiais
- conquistas

---

# Aprendizagem

O conteúdo educativo será dividido em módulos.

Exemplo:

Matemática

- Soma
- Subtração
- Multiplicação
- Divisão

Português

- Leitura
- Escrita
- Interpretação

Lógica

Memória

Raciocínio

No futuro poderão existir diversos módulos adicionais.

---

# Launcher

O aplicativo será um launcher Android completo.

Responsável por:

- substituir a Home
- listar aplicativos instalados
- abrir aplicativos
- controlar acesso
- medir tempo de uso
- aplicar regras de desbloqueio

---

# Painel dos Pais

Os responsáveis possuem um painel separado.

Podem acompanhar:

- progresso
- tempo de uso
- conquistas
- atividades realizadas
- evolução
- sequência de dias
- desempenho por disciplina

O foco deve ser acompanhamento, não vigilância.

---

# Experiência

Toda a experiência deve transmitir:

- aconchego
- diversão
- natureza
- crescimento
- conquista

Evitar aparência corporativa.

Evitar aparência tecnológica.

Evitar visual frio.

---

# Identidade Visual

## Cores

Laranja

```text
#E8703A
```

Marrom

```text
#2C2C2A
```

Bege

```text
#FDF8F4
```

Laranja claro

```text
#F2A878
```

Verdes suaves para vegetação.

---

# Estilo

Flat Design.

Material Design 3.

Bordas arredondadas.

Muito espaço em branco.

Ilustrações amigáveis.

Poucos elementos por tela.

Animações suaves.

---

# Telas Principais

- Home
- Explorar
- Aprender
- Missões
- Conquistas
- Recompensas
- Perfil
- Configurações
- Painel dos Pais

---

# Arquitetura Técnica

## Front-end

- Kotlin
- Jetpack Compose
- Material Design 3
- Hilt
- Navigation Compose
- Room
- DataStore
- Coroutines
- Flow
- Rive
- Lottie

---

## Backend

- Kotlin
- Spring Boot
- PostgreSQL
- Redis
- JWT
- Spring Security

---

# Arquitetura do Projeto

```text
app
│
├── core
├── engine
├── feature
├── services
└── build-logic
```

## Core

Contém recursos compartilhados:

- UI
- Design System
- Models
- Network
- Database
- Permissions
- Navigation
- Security

---

## Engine

Contém toda regra de negócio.

Exemplo:

- Launcher Engine
- Gamification Engine
- Education Engine
- Unlock Engine
- Rewards Engine
- Personalization Engine

Nenhuma regra de negócio deve ficar nas telas.

---

## Features

Cada funcionalidade possui seu próprio módulo.

Exemplo:

```text
feature/home
feature/explore
feature/learn
feature/profile
feature/rewards
```

Cada feature contém:

```text
presentation/
domain/
data/
di/
```

---

# Objetivos do Produto

- Tornar o aprendizado divertido.
- Reduzir o consumo passivo de telas.
- Desenvolver autonomia.
- Incentivar hábitos saudáveis.
- Recompensar esforço em vez de tempo.
- Transformar o celular em uma ferramenta de desenvolvimento.

---

# Visão de Longo Prazo

A Toca não deve ser apenas um launcher.

Ela deve evoluir para um ecossistema completo de aprendizagem infantil.

Possíveis evoluções:

- Plataforma web para pais.
- Plataforma para escolas.
- Conteúdo adaptativo com IA.
- Ranking entre amigos.
- Sistema de desafios colaborativos.
- Loja de personalização.
- Marketplace de conteúdos educativos.
- Integração com dispositivos Android dedicados.
- Expansão internacional.

---

# Princípios do Projeto

- A criança sempre vem em primeiro lugar.
- O aprendizado deve ser divertido.
- Toda recompensa deve incentivar crescimento.
- A tecnologia deve servir ao desenvolvimento humano.
- A interface deve ser simples, acolhedora e intuitiva.
- O launcher deve ser leve, rápido e confiável.
- O código deve seguir princípios de Clean Architecture, modularização e alta escalabilidade.

---

# Missão

**Criar um ambiente digital onde aprender seja tão divertido quanto brincar e onde cada minuto de tela contribua para o desenvolvimento da criança.**

# Visão

**Ser a principal plataforma de aprendizagem gamificada para crianças, transformando smartphones em ambientes seguros, educativos e inspiradores.**

# Slogan

> **Transformando tempo de tela em tempo de aprendizado.**