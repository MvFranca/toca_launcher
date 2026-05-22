# Ritmo & Compasso — Contexto do Minijogo

## Status

Planejado — ainda sem código no repositório.

## Ideia central

Único jogo no **tempo**, não no espaço. Não se "olha" um ritmo — ouve-se, guarda-se na **memória de curto prazo** e age-se. Recruta capacidades cognitivas completamente diferentes dos outros três jogos.

## Gramática rítmica

Sequências como **frases musicais**: começo, meio e fim com expectativa de coerência interna. Assim como uma frase bem formada tem sujeito e verbo, uma sequência rítmica bem formada tem **tensão e resolução**.

## Variedade

Combinação de: **duração** × **acentuação** × **pausa** → espaço de frases rítmicas muito amplo.

## Progressão de mecânica

A mesma gramática rítmica sustenta mecânicas completamente diferentes:

| Nível | Mecânica |
|-------|----------|
| Inicial | **Reproduzir** a sequência |
| Intermediário | **Completar** a sequência (gap no meio) |
| Avançado | **Identificar o erro** numa sequência modificada |
| Expert | **Criar uma variação** coerente |

## Diretrizes de implementação

- Representar ritmo como **sequência de eventos temporais** (onsets, durações, silêncios) — não áudio pré-gravado fixo por pergunta.
- **Sincronização e tolerância de timing** são parâmetros do gerador/dificuldade, não constantes hardcoded.
- Avaliar se um metrônomo/event scheduler simples resolve o MVP antes de depender de biblioteca de áudio pesada.
- Estrutura sugerida ao criar a pasta:
  ```
  app/.../minigame/ritmo/
    RhythmGenerator.kt     ← gera RhythmPhrase a partir de dificuldade
    RhythmPhrase.kt        ← lista de RhythmEvent (onset, duration, accent)
    RhythmMechanic.kt      ← sealed class: Reproduzir, Completar, IdentificarErro, CriarVariacao
    RitmoViewModel.kt
    RitmoScreen.kt
  ```
