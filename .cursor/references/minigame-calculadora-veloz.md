# Calculadora Veloz — Contexto do Minijogo

## Ideia central

O que torna um problema matemático interessante não é o tamanho do número — é a **relação entre elementos**. `7 + 5` ≠ `8 + 4` mentalmente, mesmo que o resultado seja 12.

O gerador **não escolhe números aleatórios**; escolhe **relações** que produzem um nível de **esforço cognitivo** alvo para o nível atual da criança.

## Progressão pedagógica

1. Relações resolvíveis **na cabeça com conforto** (nível inicial)
2. Relações que exigem **um passo a mais**
3. **Duas etapas** mentais
4. **Direção inversa**: dado o resultado, encontrar componentes

## Por que não acaba

- Espaço de **relações numéricas** ≈ ilimitado
- Espaço de **formatos de pergunta** multiplica isso ainda mais

## Formatos de pergunta existentes

| `QuestionFormat` | Descrição |
|------------------|-----------|
| `RESULTADO` | Quanto é A op B? |
| `OPERANDO_FALTANTE` | __ op B = R  /  A op __ = R |
| `COMPARACAO` | Qual expressão produz o maior resultado? |

## Estrutura de código atual

```
app/.../minigame/calculadora/
  QuestionGenerator.kt       ← gera Question a partir de DifficultyParams
  DifficultyManager.kt       ← decide qual DifficultyParams usar por nível
  CalculadoraVelozViewModel.kt
  CalculadoraVelozScreen.kt
  SessionResultScreen.kt
  model/
    DifficultyParams.kt      ← level, operations, rangeMin/Max, questionFormats, timeLimitSeconds
    Question.kt
    SessionResult.kt
```

## Diretrizes de implementação

- Modelar **relação** separado de **formato** — `DifficultyParams.questionFormats` já faz isso; mantenha essa separação.
- Dificuldade = combinação de relação + formato + tempo/limites, **não só `rangeMax` maior**.
- Distratores devem refletir **erros plausíveis** do processo mental. `correct ± n` genérico é aceitável nos níveis iniciais, mas em níveis avançados prefira candidatos que representem erros reais (confusão de operação, erro de carry, etc.).
- Ao estender, alinhe mudanças ao modelo de relações numéricas — não adicione lógica arbitrária em `generateOperands` sem mapear qual tipo de relação ela representa.

## Extensões previstas (ainda não implementadas)

- Propriedades de relação: "decomposição amigável", "fato básico memorizado", "distância ao múltiplo de 10"
- Operandos com decimais e frações (níveis superiores)
- Formato `RESULTADO_APROXIMADO`: estimativa antes do cálculo exato
