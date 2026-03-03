PRAGMA foreign_keys = ON;

INSERT INTO clientes (id, nome, identificador, telefone, tipo_pessoa, email, endereco) VALUES
(1,  'Joao Carlos Oliveira',         '123.456.789-01',     '(88) 99901-0001', 'FISICA',   'joao.oliveira@email.com',            'Rua das Flores, 123, Juazeiro do Norte/CE'),
(2,  'Maria Fernanda Santos',        '234.567.890-12',     '(88) 99902-0002', 'FISICA',   'maria.santos@email.com',             'Av. Leao Sampaio, 456, Juazeiro do Norte/CE'),
(3,  'Pedro Alves Costa',            '345.678.901-23',     '(88) 99903-0003', 'FISICA',   'pedro.costa@email.com',              'Rua Sao Francisco, 789, Crato/CE'),
(4,  'Ana Paula Rodrigues',          '456.789.012-34',     '(88) 99904-0004', 'FISICA',   'ana.rodrigues@email.com',            'Rua da Saudade, 321, Barbalha/CE'),
(5,  'Carlos Eduardo Lima',          '567.890.123-45',     '(88) 99905-0005', 'FISICA',   'carlos.lima@email.com',              'Rua Nova, 654, Juazeiro do Norte/CE'),
(6,  'Fernanda Cristina Sousa',      '678.901.234-56',     '(88) 99906-0006', 'FISICA',   'fernanda.sousa@email.com',           'Av. Brasil, 987, Crato/CE'),
(7,  'Roberto Mendes Pereira',       '789.012.345-67',     '(88) 99907-0007', 'FISICA',   'roberto.pereira@email.com',          'Rua do Sol, 147, Barbalha/CE'),
(8,  'Construtora ABC Ltda',         '12.345.678/0001-90', '(88) 3322-0001', 'JURIDICA', 'contato@construtoraABC.com.br',      'Av. Industrial, 100, Juazeiro do Norte/CE'),
(9,  'Empresa XYZ S/A',             '23.456.789/0001-01', '(88) 3322-0002', 'JURIDICA', 'juridico@empresaxyz.com.br',         'Rua Comercial, 200, Crato/CE'),
(10, 'Comercio DEF ME',              '34.567.890/0001-12', '(88) 3322-0003', 'JURIDICA', 'administrativo@comerciodef.com.br',  'Rua do Comercio, 300, Barbalha/CE');



INSERT INTO advogados (id, nome, identificador, telefone, tipo_pessoa, especialidade, email) VALUES
(1,  'Marcelo Augusto Ferreira',  '901.234.567-00', '(88) 99911-0001', 'FISICA', 'Direito Civil',           'marcelo.ferreira@adv.com.br'),
(2,  'Juliana Costa Mendes',      '912.345.678-01', '(88) 99912-0002', 'FISICA', 'Direito Trabalhista',     'juliana.mendes@adv.com.br'),
(3,  'Ricardo Almeida Santos',    '923.456.789-02', '(88) 99913-0003', 'FISICA', 'Direito Penal',           'ricardo.santos@adv.com.br'),
(4,  'Camila Borges Freitas',     '934.567.890-03', '(88) 99914-0004', 'FISICA', 'Direito Empresarial',     'camila.freitas@adv.com.br'),
(5,  'Alexandre Torres Lima',     '945.678.901-04', '(88) 99915-0005', 'FISICA', 'Direito de Familia',      'alexandre.lima@adv.com.br'),
(6,  'Patricia Moura Carvalho',   '956.789.012-05', '(88) 99916-0006', 'FISICA', 'Direito Tributario',      'patricia.carvalho@adv.com.br'),
(7,  'Gustavo Pires Nascimento',  '967.890.123-06', '(88) 99917-0007', 'FISICA', 'Direito Civil',           'gustavo.nascimento@adv.com.br'),
(8,  'Renata Campos Vieira',      '978.901.234-07', '(88) 99918-0008', 'FISICA', 'Direito Trabalhista',     'renata.vieira@adv.com.br'),
(9,  'Fabio Lopes Araujo',        '989.012.345-08', '(88) 99919-0009', 'FISICA', 'Direito Administrativo',  'fabio.araujo@adv.com.br'),
(10, 'Tatiana Moreira Silva',     '990.123.456-09', '(88) 99910-0010', 'FISICA', 'Direito Previdenciario',  'tatiana.silva@adv.com.br');



INSERT INTO advogado_oabs (id, advogado_id, estado, numero) VALUES
(1,  1,  'CE', '12345'),
(2,  1,  'SP', '987654'),
(3,  2,  'CE', '23456'),
(4,  3,  'CE', '34567'),
(5,  3,  'RJ', '56789'),
(6,  4,  'CE', '45678'),
(7,  5,  'CE', '56789'),
(8,  6,  'CE', '67890'),
(9,  7,  'CE', '78901'),
(10, 8,  'CE', '89012'),
(11, 9,  'CE', '90123'),
(12, 10, 'CE', '12340');



INSERT INTO processos (id, numero, tipo, status, fase, data_abertura, orgao_julgador, descricao, valor_causa, valor_condenacao, favoravel, honorarios_sucumbenciais, data_pagamento, advogado_id) VALUES
(1,  '00012345620238060050', 'CIVEL',       'ATIVO',     'CONHECIMENTO', '2023-03-15', '2a Vara Civel de Juazeiro do Norte',         'Acao de indenizacao por danos morais e materiais decorrente de acidente de transito', 25000.00,  NULL,      NULL, NULL, NULL,      1),
(2,  '00023456720238060050', 'TRABALHISTA', 'ATIVO',     'EXECUCAO',     '2023-05-20', '1a Vara do Trabalho de Juazeiro do Norte',    'Reclamacao trabalhista por verbas rescisorias nao pagas e horas extras',             15000.00, 12000.00, 1,    1200.00, '2024-03-20',  2),
(3,  '00034567820228060001', 'CRIMINAL',    'ENCERRADO', 'RECURSAL',     '2022-08-10', '1a Vara Criminal de Juazeiro do Norte',       'Acao penal por estelionato com absolvicao do reu em primeira instancia',             NULL,      NULL,     1,    NULL, NULL,      3),
(4,  '00045678920238060051', 'CIVEL',       'ARQUIVADO', 'LIQUIDACAO',   '2023-01-05', '1a Vara Civel de Barbalha',                   'Execucao de titulo extrajudicial referente a contrato de prestacao de servicos',     8500.00,   8500.00,  1,    850.00, '2025-03-05',   4),
(5,  '00056789020248060050', 'TRABALHISTA', 'ATIVO',     'CONHECIMENTO', '2024-02-14', '2a Vara do Trabalho de Juazeiro do Norte',    'Reclamacao trabalhista por assedio moral e rescisao indireta do contrato',           20000.00,  NULL,     NULL, NULL, NULL,      5),
(6,  '00067890120238060002', 'CIVEL',       'SUSPENSO',  'EXECUCAO',     '2023-07-30', '2a Vara Civel de Crato',                      'Acao de cobranca de contrato de empreitada suspenso aguardando pericia tecnica',     45000.00,  NULL,     NULL, NULL, NULL,      6),
(7,  '00078901220228060001', 'CRIMINAL',    'ATIVO',     'CONHECIMENTO', '2022-11-22', '2a Vara Criminal de Juazeiro do Norte',       'Acao penal por trafico de drogas em fase de instrucao processual',                  NULL,      NULL,     NULL, NULL, NULL,      7),
(8,  '00089012320248060050', 'CIVEL',       'ATIVO',     'RECURSAL',     '2024-01-10', '3a Vara Civel de Juazeiro do Norte',          'Acao de rescisao contratual com pedido de devolucao de valores pagos',              120000.00, 95000.00, 1,    9500.00, '2025-05-05',  8),
(9,  '00090123420238060051', 'TRABALHISTA', 'ENCERRADO', 'EXECUCAO',     '2023-04-18', '1a Vara do Trabalho de Barbalha',             'Execucao de sentenca trabalhista com deposito judicial efetivado',                  18000.00,  16500.00, 1,    1650.00, '2024-03-12',  9),
(10, '00001234520248060001', 'CRIMINAL',    'SUSPENSO',  'CONHECIMENTO', '2024-03-01', '1a Vara Criminal de Crato',                   'Acao penal por crimes contra a administracao publica com reu preso preventivamente', NULL,      NULL,     NULL, NULL, NULL,     10);


INSERT INTO contratos (id, data_contratacao, data_encerramento, tipo_valor, valor, descricao, observacoes, cliente_id, processo_id) VALUES
(1,  '2023-03-10', NULL,         'FIXO',       5000.00,  'Contrato de representacao em acao de indenizacao',      'Pagamento em 3 parcelas mensais',        1,  1),
(2,  '2023-05-15', NULL,         'PERCENTUAL', 20.00,    'Contrato de representacao em reclamacao trabalhista',   'Percentual sobre o valor da condenacao', 2,  2),
(3,  '2022-08-01', '2023-06-30', 'FIXO',       8000.00,  'Contrato de defesa em acao penal',                     'Encerrado com absolvicao do cliente',    3,  3),
(4,  '2023-01-01', '2023-12-31', 'FIXO',       3000.00,  'Contrato de representacao em execucao de titulo',      'Processo arquivado apos quitacao',       4,  4),
(5,  '2024-02-10', NULL,         'PERCENTUAL', 25.00,    'Contrato de representacao em reclamacao por assedio',  'Honorarios de exito de 25%',             5,  5),
(6,  '2023-07-25', NULL,         'FIXO',       12000.00, 'Contrato de representacao em acao de cobranca',        'Inclui acompanhamento de pericia',       6,  6),
(7,  '2022-11-15', NULL,         'FIXO',       15000.00, 'Contrato de defesa criminal - trafico de drogas',      'Pagamento mensal de R$ 1.500,00',        7,  7),
(8,  '2024-01-05', NULL,         'PERCENTUAL', 15.00,    'Contrato de representacao em acao de rescisao',        'Percentual sobre valores recuperados',   8,  8),
(9,  '2023-04-10', '2024-01-15', 'PERCENTUAL', 20.00,    'Contrato de execucao de sentenca trabalhista',         'Processo encerrado com exito',           9,  9),
(10, '2024-02-28', NULL,         'FIXO',       20000.00, 'Contrato de defesa criminal na administracao publica', 'Pagamento antecipado integral',          10, 10);


INSERT INTO audiencias (id, descricao, data_hora, local, tipo, status, resultado, observacoes, processo_id) VALUES
(1,  'Audiencia de Conciliacao - Indenizacao por acidente',        '2023-06-20 09:00:00', '2a Vara Civel de Juazeiro do Norte',                   'CONCILIACAO', 'REALIZADA',  'Acordo nao obtido, prosseguimento da instrucao',              'Partes presentes|Tentativa de acordo fracassada|Designada audiencia de instrucao', 1),
(2,  'Audiencia de Instrucao - Verbas Trabalhistas',               '2023-09-14 14:00:00', '1a Vara do Trabalho de Juazeiro do Norte',              'INSTRUCAO',   'REALIZADA',  'Depoimento das testemunhas colhido, aguardando sentenca',     'Tres testemunhas ouvidas|Documentos juntados|Prazo para memoriais', 2),
(3,  'Audiencia de Julgamento - Recurso Criminal',                 '2023-04-25 10:00:00', 'Tribunal de Justica do Ceara - Camara Criminal',        'JULGAMENTO',  'REALIZADA',  'Absolvicao mantida por unanimidade',                         'Sessao de julgamento do recurso ministerial|Placar 3x0 favoravel a defesa', 3),
(4,  'Audiencia Preliminar - Execucao de Titulo',                  '2023-03-10 08:30:00', '1a Vara Civel de Barbalha',                             'PRELIMINAR',  'REALIZADA',  'Citacao do executado realizada, prazo para embargos',        'Executado citado pessoalmente|Prazo de 15 dias para embargos', 4),
(5,  'Audiencia de Conciliacao - Assedio Moral',                   '2024-05-08 09:30:00', '2a Vara do Trabalho de Juazeiro do Norte',              'CONCILIACAO', 'AGENDADA',   NULL,                                                         'Primeira tentativa de conciliacao|Partes notificadas por AR', 5),
(6,  'Audiencia de Instrucao - Acao de Cobranca',                 '2024-04-22 15:00:00', '2a Vara Civel de Crato',                                'INSTRUCAO',   'CANCELADA',  NULL,                                                         'Cancelada por ausencia do perito nomeado|Remarcar em nova data', 6),
(7,  'Audiencia de Instrucao - Acao Penal Trafico',               '2023-08-30 08:00:00', '2a Vara Criminal de Juazeiro do Norte',                 'INSTRUCAO',   'REALIZADA',  'Ouvidas 4 testemunhas de acusacao, instrucao continua',      'Reu presente|Defensor constituido presente|4 testemunhas ouvidas', 7),
(8,  'Sessao de Julgamento - Recurso Civel',                      '2024-03-18 10:00:00', 'Tribunal de Justica do Ceara - 3a Camara Civel',        'JULGAMENTO',  'REALIZADA',  'Recurso provido parcialmente, valor reduzido para R$ 95.000', 'Sessao presidida pelo Des. Relator|Votacao unanime', 8),
(9,  'Audiencia de Julgamento - Execucao Trabalhista',            '2023-11-05 09:00:00', '1a Vara do Trabalho de Barbalha',                       'JULGAMENTO',  'REALIZADA',  'Deposito judicial realizado, processo encaminhado para baixa', 'Credor levantou os valores|Devedor quitou integralmente', 9),
(10, 'Audiencia Preliminar - Crime Contra a Administracao',        '2024-04-15 14:00:00', '1a Vara Criminal de Crato',                             'PRELIMINAR',  'CANCELADA',  NULL,                                                         'Cancelada por habeas corpus preventivo|Aguardando decisao do STJ', 10);


INSERT INTO interessados (id, nome, identificador, telefone, tipo_pessoa, tipo_parte, processo_id, advogado_id) VALUES
(1,  'Seguradora Nacional S/A',       '45.678.901/0001-23', '(88) 3300-1000',  'JURIDICA', 'REU',                  1,  4),
(2,  'Mercado Bom Preco Ltda',        '56.789.012/0001-34', '(88) 3301-2000',  'JURIDICA', 'REU',                  2,  NULL),
(3,  'Ministerio Publico do Ceara',   NULL,                 '(88) 3302-3000',  'JURIDICA', 'AUTOR',                3,  NULL),
(4,  'Jose Ferreira Neto',            '111.222.333-44',     '(88) 99930-0004', 'FISICA',   'REU',                  4,  NULL),
(5,  'Supermercado Atacadao ME',      '67.890.123/0001-45', '(88) 3303-4000',  'JURIDICA', 'REU',                  5,  NULL),
(6,  'Engenharia Sul Ltda',           '78.901.234/0001-56', '(88) 3304-5000',  'JURIDICA', 'REU',                  6,  9),
(7,  'Ministerio Publico do Ceara',   NULL,                 '(88) 3302-3000',  'JURIDICA', 'AUTOR',                7,  NULL),
(8,  'Imobiliaria Lar e Conforto',    '89.012.345/0001-67', '(88) 3305-6000',  'JURIDICA', 'REU',                  8,  6),
(9,  'Industria Metalica Norte ME',   '90.123.456/0001-78', '(88) 3306-7000',  'JURIDICA', 'REU',                  9,  NULL),
(10, 'Ministerio Publico do Ceara',   NULL,                 '(88) 3302-3000',  'JURIDICA', 'AUTOR',                10, NULL),
(11, 'Dr. Antonio Jose Figueiredo',   '222.333.444-55',     '(88) 99931-0011', 'FISICA',   'PERITO',               6,  NULL),
(12, 'Banco do Brasil S/A',           '00.000.000/0001-91', '(88) 3307-8000',  'JURIDICA', 'TERCEIRO_INTERESSADO', 4,  NULL);


INSERT INTO testemunhas (id, nome, identificador, telefone, tipo_pessoa, depoimento) VALUES
(1,  'Antonio Souza Barbosa',     '444.555.666-77', '(88) 99941-0001', 'FISICA', 'Presenciei o acidente na Av. Leao Sampaio quando o veiculo avancou o sinal vermelho e colidiu com o automovel do autor'),
(2,  'Sandra Lima Vasconcelos',   '555.666.777-88', '(88) 99942-0002', 'FISICA', 'Trabalhei no setor de RH e confirmo que o reclamante nunca recebeu aviso previo escrito nem suas verbas rescisorias'),
(3,  'Paulo Cesar Matos',         '666.777.888-99', '(88) 99943-0003', 'FISICA', 'Estava presente quando o contrato foi firmado e testemunhei a entrega dos valores ao acusado'),
(4,  'Luciana Freitas Gomes',     '777.888.999-00', '(88) 99944-0004', 'FISICA', 'A empresa devedora sempre reconheceu a divida em nossas reunioes de diretoria mas postergava o pagamento'),
(5,  'Francisco Neto Cavalcante', '888.999.000-11', '(88) 99945-0005', 'FISICA', 'Fui supervisor da reclamante e nunca presenciei qualquer conduta de assedio moral no ambiente de trabalho'),
(6,  'Beatriz Alves Correia',     '999.000.111-22', '(88) 99946-0006', 'FISICA', 'Confirmei em laudo tecnico que a obra possui vicios de construcao graves e incompativeis com as normas ABNT'),
(7,  'Marcos Henrique Diniz',     '000.111.222-33', '(88) 99947-0007', 'FISICA', 'Vi o acusado na localidade na data dos fatos porem nao posso confirmar o ato criminoso imputado na denuncia'),
(8,  'Claudia Regina Pinto',      '100.200.300-44', '(88) 99948-0008', 'FISICA', 'Sou vizinha da empresa e nunca observei atividades suspeitas no local nem movimentacao atipica de pessoas'),
(9,  'Raimundo Nonato Bezerra',   '200.300.400-55', '(88) 99949-0009', 'FISICA', 'Como ex-colega de trabalho confirmo que todas as verbas foram pagas no prazo e que o empregado assinou os recibos'),
(10, 'Irene Monteiro Castro',     '300.400.500-66', '(88) 99940-0010', 'FISICA', 'Sou servidora publica e nao reconheco o reu como servidor do orgao indicado na denuncia nem o vi nas dependencias');


INSERT INTO testemunha_interessado (id, testemunha_id, interessado_id, processo_id) VALUES
(1,  1,  1,  1),
(2,  2,  2,  2),
(3,  3,  3,  3),
(4,  4,  4,  4),
(5,  5,  5,  5),
(6,  6,  11, 6),
(7,  7,  7,  7),
(8,  8,  8,  8),
(9,  9,  9,  9),
(10, 10, 10, 10);


INSERT INTO documentos (id, titulo, descricao, caminho_arquivo, tipo, data_upload, is_assinado, is_protocolado, numero_protocolo, is_urgente, processo_id) VALUES
(1,  'Peticao Inicial - Indenizacao',         'Peticao inicial com pedidos de danos morais e materiais',               NULL, 'PETICAO',    '2023-03-15', 1, 1, 'PROT-2023-001234', 0, 1),
(2,  'CTPS e Contracheques - Trabalhista',    'Documentos comprobatorios do vinculo empregaticio e verbas devidas',    NULL, 'OUTRO',      '2023-05-20', 0, 1, 'PROT-2023-002345', 0, 2),
(3,  'Sentenca Absolutoria - Criminal',       'Sentenca de absolvicao proferida pelo juizo de primeiro grau',          NULL, 'SENTENCA',   '2023-01-15', 1, 1, 'PROT-2023-000345', 0, 3),
(4,  'Titulo Executivo - Contrato de Servico','Contrato de prestacao de servicos com valor liquido e certo',           NULL, 'CONTRATO',   '2023-01-05', 1, 1, 'PROT-2023-004567', 0, 4),
(5,  'Procuracao ad judicia - Assedio Moral', 'Procuracao outorgada pela reclamante ao advogado para representacao',   NULL, 'PROCURACAO', '2024-02-14', 1, 1, 'PROT-2024-005678', 0, 5),
(6,  'Laudo Pericial - Vicio de Obra',        'Laudo tecnico de engenharia civil apontando vicios construtivos graves',NULL, 'LAUDO',      '2023-10-30', 1, 1, 'PROT-2023-006789', 1, 6),
(7,  'Denuncia Criminal - Trafico de Drogas', 'Denuncia oferecida pelo Ministerio Publico do Estado do Ceara',         NULL, 'PETICAO',    '2022-11-22', 1, 1, 'PROT-2022-007890', 0, 7),
(8,  'Apelacao Civel - Rescisao Contratual',  'Recurso de apelacao interposto pela parte contraria contra a sentenca', NULL, 'RECURSO',    '2024-01-10', 1, 1, 'PROT-2024-008901', 1, 8),
(9,  'Comprovante de Deposito Judicial',      'Comprovante de deposito judicial do valor total devido ao reclamante',  NULL, 'OUTRO',      '2023-10-20', 0, 1, 'PROT-2023-009012', 0, 9),
(10, 'Pedido de Habeas Corpus Preventivo',    'Pedido de HC preventivo impetrado perante o Superior Tribunal de Justica', NULL, 'RECURSO', '2024-03-01', 1, 1, 'PROT-2024-000123', 1, 10);


INSERT INTO movimentacoes (id, descricao, data_hora, responsavel, tipo, processo_id) VALUES
(1,  'Distribuicao da peticao inicial e conclusao ao juiz para apreciacao da liminar requerida',    '2023-03-15 09:00:00', 'Escrivao Joao Paulo',       'JUNTADA',  1),
(2,  'Recebimento da reclamacao trabalhista e designacao de audiencia de conciliacao e julgamento', '2023-05-20 10:30:00', 'Diretor de Secretaria',     'DESPACHO', 2),
(3,  'Acordao do Tribunal mantendo a absolvicao do reu por unanimidade de votos dos desembargadores','2023-06-30 14:00:00','Des. Relatora Maria Lucia', 'DECISAO',  3),
(4,  'Sentenca de extincao da execucao com resolucao do merito apos quitacao integral da divida',   '2023-12-10 11:00:00', 'Juiz Titular da 1a Vara',   'SENTENCA', 4),
(5,  'Juntada de documentos pelo reclamante: CTPS, holerites dos ultimos 12 meses e comunicado de dispensa', '2024-02-20 08:30:00', 'Escrivao Carlos', 'JUNTADA',  5),
(6,  'Decisao suspendendo o processo ate conclusao da pericia tecnica de engenharia civil',         '2023-09-05 16:00:00', 'Juiza da 2a Vara Civel',    'DECISAO',  6),
(7,  'Interrogatorio do reu realizado com assistencia do defensor constituido e presenca do MP',    '2023-07-12 09:00:00', 'Juiz Criminal Titular',     'AUDIENCIA',7),
(8,  'Acordao do TJCE reformando parcialmente a sentenca de primeiro grau e reduzindo o valor',     '2024-03-18 10:00:00', 'Des. Presidente da Camara', 'DECISAO',  8),
(9,  'Expedicao do alvara de levantamento do deposito judicial em favor do reclamante vencedor',    '2023-11-10 14:00:00', 'Juiza do Trabalho',         'DESPACHO', 9),
(10, 'Despacho comunicando a suspensao do processo por forca de decisao liminar proferida pelo STJ','2024-03-05 15:00:00', 'Assessor Juridico',         'DESPACHO', 10);
