# Mistura de Cores — Contexto do Minijogo

## Status

Planejado — ainda sem código no repositório.

## Ideia central

Conteúdo **perceptivo**, não abstrato. A criança não está calculando — está observando **consequências**. Ativa raciocínio causal: *se faço X, acontece Y*.

## Relações e direções de pergunta

| Apresentação | Descrição |
|-------------|-----------|
| Direta | Duas cores entram → qual cor sai? |
| Inversa | Dada a cor resultante → quais cores entraram? |
| Meio | Dado início e fim → qual foi o passo do meio? |

A mesma lógica de mistura gera três famílias de pergunta completamente diferentes.

## Por que não acaba

Combinações de: cores × proporções × direção da pergunta são enormes. Mas o diferencial está na **discriminação perceptiva** — nos níveis avançados, as opções erradas são tons tão próximos do correto que a criança precisa realmente *enxergar* a diferença.

## Dificuldade avançada

Não é só sobre complexidade lógica — é sobre **discriminação perceptiva**. Em níveis iniciais, as opções são claramente distintas. Em níveis avançados, os distratores são variações sutis no espaço de cor (hue, saturação, luminosidade).

## Diretrizes de implementação

- Modelar mistura como **função matemática** (ex.: mistura aditiva RGB, proporções ponderadas), não como sprites ou lookup tables fixas.
- Gerar distratores no **espaço de cor perceptivo** (LAB ou HSL), não com IDs arbitrários — o grau de proximidade perceptiva controla a dificuldade.
- Testar **acessibilidade**: contraste mínimo entre opções sem tornar triviais nos níveis baixos.
- Estrutura sugerida ao criar a pasta:
  ```
  app/.../minigame/cores/
    ColorMixer.kt          ← lógica de mistura e geração de distratores
    ColorQuestion.kt       ← sealed class com as três direções
    MisturaViewModel.kt
    MisturaScreen.kt
  ```
