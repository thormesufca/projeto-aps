# Gerenciamento de Audiências e Processos Judiciais

### Objetivo
O presente sistema foi desenvolvido como parte da disciplina Análise e Projeto de Sistemas, da Universidade Federal do Cariri, no semestre 2025.2

### Estrutura
O sistema foi elaborado para manter uma estrutura centrada no processo judicial, com cadastro de vários campos e entidades vinculadas a ele, conforme se pode verificar do diagrama de classe de domínio abaixo:
![Diagrama de Classes - Domínio](./diagramas/Diagrama%20de%20Classes%20-%20Domínio.png "Diagrama de Classes")

Como se verifica, o sistema foi elaborado para vincular-se a um escritório de advocacia, onde seus membros poderão registrar diversos dados para controle. As entidades elaboradas foram:
### Entidades Judiciais
 - Processo - Mantém os dados do processo, possuindo diversos atributos limitados, tais como Tipo (Cível, Criminal, Trabalhista), Fase (Conhecimento, Execução, Recursal, Liquidação), Status (Ativo, Arquivado, Encerrado, Suspenso).
 - Audiência - Um processo pode ter mais de uma audiência, com possibilidade de registro de observações sobre o ocorrido e qual o resultado da audiência.
 - Movimentação - A forma de o processo se movimentar, se está destinado a alguma das partes ou ao próprio juízo
 - AssuntoProcesso - Entidade que vincula quais assuntos são vinculados ao processo (Os dados do assunto pertencem a uma tabela do CNJ, junto do projeto)
 - Documento - São os documentos anexados ao processo, geralmente em juntados em sequencia no processo
 - Pessoa - Classe abstrata que guarda as informações básicas das pessoas e diversas formas como podem se vincular aos processos:
   - Cliente - A pessoa que contrata e recebe os serviços do escritório
   - Advogado - O membro do escritório ou profissional que atua no processo representando alguma parte (seja membro do escritório ou não)
   - Interessado - Pessoa que participa do processo judicial, seja como autor, réu ou terceiro interessado
   - Testemunha - Pessoa que é arrolada por uma das partes para prestar depoimento no processo

### Entidades Administrativas
- MembroEscritório - É o advogado que é membro do escritório de advocacia gerenciador do sistema, podendo ser sócio ou empregado do escritório
- Contrato - É o vínculo que existe entre o cliente e o escritório. Um cliente pode ter vários contratos com o escritório, sendo que cada contrato se vincula a um processo judicial em específico.
- InscricaoOAB - Dado que serve para registrar as inscrições na OAB dos advogados, tendo em vista que cada advogado pode ter inscrição na OAB da Seccional de cada um dos estados da federação.

## Ações Previstas
O ator principal do sistema será o advogado ou agente administrativo do escritório, que registrará as informações no sistema para fins de gerenciamento e acompanhamento pelos membros do escritório, conforme diagrama de ações previsto:
![Diagrama de Caso de Uso](./diagramas/Diagrama%20de%20Casos%20de%20Uso.png "Diagrama de Casos de Uso")

## Padrões de Projeto
O presente projeto tem como objetivo precípuo o exercício prático de implementação de padrões de projeto. Dessa forma, entre os padrões estudados na disciplina, vislumbrou-se aplicação dos seguintes padrões:
- Singleton - Utilizado para instanciar a conexão com o banco de dados (SQLITE)
- Factory Method - Utilizado para criar um processo, passando seu tipo como string. A depender do tipo de processo, ao criar o processo, seu primeiro movimento é modificado.
- Facade - Classe única que gerencia todas as conexões das classes e serviços. Além das classes de domínio, há as classes de serviço que são responsáveis por validar os dados recebidos e fazer a persistência
- Decorator - Foi eleito o documento como objeto passível de decoração. O documento pode ser decorado com anotação de assinatura (quem assinou), de urgência (motivo da urgência), protocolo (id e data de protocolo)
- Adapter - Para implementação desse padrão, imagina-se a criação de serviço de email legado, que recebe três parâmetros para o envio de e-mail, sendo adaptado por uma nova classe que consegue enviar o email recebendo apenas dois parâmetros
- Observer - Utilizado para notificações sobre o registro de uma audiência. Ao cadastrar uma audiência, o sistema deve notificar o advogado responsável por aquele processo, bem como enviar uma mensagem de log, para registros.
- Strategy - Pode-se utilizar esse padrão para escolher a melhor estratégia para a busca do processo, tais como por número, por cliente, por status, etc.
- Template - Foram criados relatórios diversos para aplicar o template method, que deverá produzir um cabeçalho e rodapé unificado para o sistema, alterando-se os dados do corpo do relatório a partir do template escolhido.

É possível ver o mapeamento dos padrões e classes para sua aplicação no diagrama de classes abaixo:
![Diagrama de Classes - Padrões](./diagramas/Diagrama%20de%20Classes%20-%20Padroes%20de%20Projeto.png "Diagrama de Padrões de Projeto")

### Cadastro de Processo
Tratando-se da entidade que centra os demais dados, elaborou-se um diagrama sequencial de quais classes serão utilizadas na criação do processo, conforme descrito no diagrama abaixo:
![Diagrama de Sequência - Criação de Processo](./diagramas/Diagrama%20de%20Sequencia%20-%20Cadastro%20de%20Processo.png "Diagrama de Sequência - Criar Processo")

### Instalação do sistema
Considerando que o sistema utilizará Sqlite para persistência de dados, é recomendável seguir as instruções continas em (`./lib/LEIA-ME.txt`) para download do arquivo `.jar`. Seque ainda arquivo `./lib/assuntos.json` contendo a tabela de assuntos do CNJ, para cadastro.