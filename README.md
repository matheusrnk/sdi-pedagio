# *PEDÁGIOS - SDI*

### Visualização alto-nível do sistema:

![System Design image](https://raw.githubusercontent.com/matheusrnk/sdi-pedagio/refs/heads/main/systemdesign.svg)

### Como rodar a aplicação?

    Obs. 1: É importante ter o VSCode na máquina e o pacote "Extension Pack for Java" instalados.

    Conectado a rede de ensino no VSCode, abra o diretório contendo a raiz do projeto:

        "sdi-pedagio/pedagio/"

    Abra as seguintes pastas no "explorer":

        "src/main/java/udesc/trabalho/consumer/"

        "src/main/java/udesc/trabalho/producer/"
    
    Para rodar a aplicação, se tem dois arquivos principais:

        "consumer/Server.java"

        "producer/Client.java"
    
    Basta ir no código fonte e rodar a aplicação, seja ela utilizando o botão F5 ou indo diretamente no arquivo e apertando o botão de "run".

    Rode primeiro o servidor e depois o cliente.

    Obs. 2: O projeto foi gerado com MAVEN, portanto, é possível rodá-lo através do terminal. Entretanto, não se recomenda fazer isto, uma vez que vários passos adicionais serão necessários para configurar tudo corretamente.





### Onde é possível acompanhar a execução?
    Tanto o diretório "consumer" quanto o diretório "producer" possuem classes responsáveis por enviar mensagens a logs que serão gerados durante a execução.
    Cada um terá o seu log, portanto, ao final do processamento o diretório raiz possuirá mais dois arquivos: "producer.log.*" e "consumer.log.*".

    Obs.: O log do consumidor possuirá informações "extras" relacionadas a consulta da API do RabbitMQ, a fim de saber qual o tamanho das filas para
    balancear os consumidores. Portanto, caso ache a informação desnecessária, 
    basta comentar o trecho de código nas linhas 56 e 58 do caminho "pedagio/src/main/java/udesc/trabalho/consumer/ExchangeMonitor.java"