# Sequência Secreta — Contexto do Minijogo

## Status

Planejado — ainda sem código no repositório.

## Ideia central

Uma sequência é uma **história com lógica oculta**. A criança não responde uma pergunta — **descobre uma regra** que alguém escondeu. Isso muda a relação emocional com o jogo: não é certo ou errado; é *descoberto* ou *ainda não descoberto*.

## Gramática dos padrões

Assim como palavras combinam segundo regras para formar frases, **células de padrão** combinam segundo regras para formar sequências.

| Dimensão | Exemplos |
|----------|----------|
| Tipo de célula | Numérica, visual (forma/cor), rítmica, espacial |
| Tipo de regra | Progressão aritmética, alternância, espelhamento, composição |
| Posição do gap | Início, meio, fim — varia por nível |

## Progressão

1. Padrão único com gap em posição óbvia
2. Gap em posição menos óbvia
3. **Segunda camada sobreposta** — duas lógicas simultâneas na mesma sequência

## Por que não acaba

Combinações de: tipo de célula × tipo de regra × posição do gap → na prática nunca se repetem.

## Diretrizes de implementação

- Pensar em **gramática formal ou AST** (regra → sequência), não em lista de sequências hardcoded.
- UI deve reforçar sensação de **investigação**, não quiz punitivo — evitar vermelho/errado explícito antes da criança decidir.
- Exportar a "regra descoberta" como metadado opcional para feedback positivo ("Você descobriu: +3 a cada passo!").
- Estrutura sugerida ao criar a pasta:
  ```
  app/.../minigame/sequencia/
    PatternGenerator.kt
    PatternRule.kt        ← sealed class com os tipos de regra
    SequenceCell.kt       ← sealed class com os tipos de célula
    SequenciaViewModel.kt
    SequenciaScreen.kt
  ```
