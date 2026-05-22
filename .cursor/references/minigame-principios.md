# Princípios — Geração Procedural

Contexto compartilhado por todos os quatro minijogos do Domo. Leia este arquivo sempre que uma tarefa tocar mais de um jogo ou conceitos transversais.

## O que é um gerador procedural

Um gerador procedural é um **conjunto de regras** que, dado um ponto de partida, produz uma **experiência única**. Ele não sabe de antemão o que vai criar — segue regras e o resultado **emerge**.

**Metáfora do dado:**
- 1 dado de 6 faces → 6 resultados
- 2 dados → 36 combinações
- 3 dados com regras de interação → centenas de situações

A complexidade da experiência cresce **muito mais rápido** que a complexidade das regras.

## Princípio unificador dos quatro jogos

> Regras simples, espaço de experiências amplo.

A criança não deve perceber um "sistema gerado" — deve sentir que **descobre algo novo** a cada vez.

| Abordagem | Resultado |
|-----------|-----------|
| Banco de perguntas grande | Esgota |
| Gerador bem pensado | Habita |

## O que une os quatro

Todos exploram o mesmo princípio: a dificuldade não sobe pelo tamanho dos números ou pela quantidade de conteúdo fixo — sobe pela **complexidade das relações** e pela **mudança de mecânica**.

## Checklist universal ao revisar código de gerador

- [ ] A variedade vem de **regras composicionais**, não de N entradas fixas?
- [ ] A dificuldade sobe por **tipo de relação/mecânica**, não só por valores maiores?
- [ ] O formato da pergunta inverte ou rotaciona a tarefa (frente / trás / meio)?
- [ ] A experiência parece **descoberta**, não prova esgotável?
- [ ] Distratores / alternativas erradas são **pedagogicamente plausíveis**?
- [ ] O código separa **geração** (regras) de **apresentação** (UI)?

## Status dos minijogos no repositório

| Jogo | Status | Pasta no código |
|------|--------|-----------------|
| Calculadora Veloz | Implementado | `app/.../minigame/calculadora/` |
| Sequência Secreta | Planejado | — |
| Mistura de Cores | Planejado | — |
| Ritmo & Compasso | Planejado | — |
