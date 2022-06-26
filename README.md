# Bem vindo ao GeoDistanceLocation

Api desenvolvida em spring boot integrada com os serviços de localização do google, **divirta-se**. 


# Apikey

Para poder utilizar o **GeoDistanceLocation** tenha uma **apikey** do google maps. Você poderá conseguir uma [aqui](https://cloud.google.com/) e caso tenha alguma dificuldade existe este [guia](https://maplink.global/blog/como-obter-chave-api-google-maps/) que poderá ser usado para obter a chave.

# Rodando o projeto localmente.

Com apikey do google, podemos nos preparar para a execução do  projeto. Existem duas formas de usar a apikey do google, passando ela na requisição. ou adicionando ela como uma variável de ambiente, para tal devemos adicionar em nosso sistemas as seguintes variáveis :
> ENV_PROFILE com o valor env
> ENV_GOOGLE_API_KEY com o valor da apykey obtida

Existem algumas formas de rodar o projeto localmente, aqui sugerimos 3 opçoes;

## Rodando o projeto em uma Ide

Tenha uma IDE compatível com gradle e spring Boot, Comas devidas dependências do projeto baixadas é só iniciar o projeto  como um spring boot app.

## Rodando o projeto via commandline

Para rodar o projeto via commandLine devemos executar o seguinte código  na raiz do projeto, ele ira realizar o build e logo em seguida iniciar a API.
>./gradlew build && java -jar build/libs/CalindraGeo-0.0.1-SNAPSHOT.jar

## Rodando em containerDocker

Para rodar o projeto via container docker devemos executar os seguintes códigos na raiz do projeto.
>./gradlew build
>docker build --build-arg JAR_FILE=build/libs/\*.jar -t calindra/geo-location-docker .
>docker run -p 8080:8080 calindra/geo-location-docker

Podemos passar as variáveis de ambiente para o nosso container na hora de sua execução, para isso podemos montar um arquivo  .env na raiz do projeto como este exemplo :
>ENV_PROFILE=env
>ENV_GOOGLE_API_KEY=minhaAPIKEY

Criado o arquivo executar o seguinte comando
>docker run --env-file .env -p 8080:8080 calindra/geo-location-docker
>
**obs**: altere as portas se for necessário

# Executando as APIs disponiveis

Para executar as APIS foi disponibilizado um swagger com o seguinte endpoint /swagger-ui/#/ para acessalo um exemplo é ir até http://localhost:8081/swagger-ui/#/
* (*) Parâmetros obrigatorios
* (**) Parâmetros obrigatórios caso não tenha configurado a apikey como variável de ambiente

### /api/geolocation/address

Envie endereços separados por ponto e virgula e obtenha as suas coordenadas.
parâmetros address* e apikey**
Ex:
>/api/geolocation/address?address= Bom jesus do itabapoana, Lia marcia

Response:
	
~~~json
[  {  "address":  "Lia Márcia, Bom Jesus do Itabapoana - RJ, Brasil",  "lat":  -21.1327492,  "lng":  -41.6680941  }  ]
~~~

### /api/geolocation/geopoint
Envie coordenadas e obtenha uma lista de endereços que possuem tais cordenadas
parâmetros latitude* e latitude* apikey**
  Ex:
  >/api/geolocation/geopoint?latitude=-21.1327492&longitude=-41.6680941

Response:
~~~json
[  {  "address":  "Av. Gov. Roberto Silveira, 1173 - Lia Márcia, Bom Jesus do Itabapoana - RJ, 28360-000, Brasil",  "lat":  -21.1327934,  "lng":  -41.6681932  },  {  "address":  "Av. Gov. Roberto Silveira, 1197 - Residencia Parque das Aguas, Bom Jesus do Itabapoana - RJ, 28360-000, Brasil",  "lat":  -21.1326426,  "lng":  -41.66813339999999  },  {  "address":  "R. Itaperuna, 187 - Espirito Santo, Bom Jesus do Itabapoana - RJ, 28360-000, Brasil",  "lat":  -21.1326899,  "lng":  -41.6680863  },  {  "address":  "Av. Gov. Roberto Silveira, 204-272 - Residencia Parque das Aguas, Bom Jesus do Itabapoana - RJ, 28360-000, Brasil",  "lat":  -21.1326757,  "lng":  -41.66844  },  {  "address":  "V88J+WQ Bom Jesus do Itabapoana, RJ, Brasil",  "lat":  -21.1327492,  "lng":  -41.6680941  },  {  "address":  "Espirito Santo, Bom Jesus do Itabapoana - RJ, Brasil",  "lat":  -21.1312895,  "lng":  -41.6697588  },  {  "address":  "Bom Jesus do Itabapoana, RJ, 28360-000, Brasil",  "lat":  -21.1362798,  "lng":  -41.677214  },  {  "address":  "Bom Jesus do Itabapoana, RJ, 28360-000, Brasil",  "lat":  -21.1330059,  "lng":  -41.7407394  },  {  "address":  "Bom Jesus do Itabapoana - RJ, 28360-000, Brasil",  "lat":  -21.1386404,  "lng":  -41.6783696  },  {  "address":  "Bom Jesus do Itabapoana - RJ, 28360-000, Brasil",  "lat":  -21.1330059,  "lng":  -41.7407394  },  {  "address":  "Rio de Janeiro, Brasil",  "lat":  -22.3534263,  "lng":  -42.7076107  },  {  "address":  "Brasil",  "lat":  -14.235004,  "lng":  -51.92528  }  ]
~~~


### /api/distances
Envie endereços e obtenha a distancia entre eles.
parâmetros origins*(lista de endereços separados por ; )  e destinations*(lista de endereços sepárados por ; )  apikey**
  Ex: 
  >/api/distances?origins=Bom jesus do itabapoana&destinations=Belo horizonte&apikey=Suaapikey

Response:
~~~json
[  {  "originAddress":  "Belo Horizonte, MG, Brasil",  "originLocation":  {  "address":  "Belo Horizonte, MG, Brasil",  "lat":  -19.919052,  "lng":  -43.9386685  },  "destinationAddress":  "Belo Horizonte, MG, Brasil",  "destinationLocation":  {  "address":  "Belo Horizonte, MG, Brasil",  "lat":  -19.919052,  "lng":  -43.9386685  },  "distanceValue":  0,  "distanceTimeSeconds":  0,  "distanceValueDescription":  "1 m",  "distanceTimeDescription":  "1 min"  }  ]
~~~

### /api/distances/matrix
Envie uma lista de endereços e obtenha qual endereço dessa lista  possuem a maior e a menor distancia entre si, obs enviar ao menos 2 endereços e distancias entre mesmo endereços serão descartados. 
Ex: 
>/api/distances/matrix?addressList=Bom jesus do itabapoana; Salvador , Bahia;Belo horizonte, Minas

Response:
~~~json
[  {  "originAddress":  "Bom Jesus do Itabapoana - RJ, 28360-000, Brasil",  "originLocation":  {  "address":  "Bom Jesus do Itabapoana, RJ, 28360-000, Brasil",  "lat":  -21.1362798,  "lng":  -41.677214  },  "destinationAddress":  "Belo Horizonte, MG, Brasil",  "destinationLocation":  {  "address":  "Belo Horizonte, MG, Brasil",  "lat":  -19.919052,  "lng":  -43.9386685  },  "distanceValue":  403867,  "distanceTimeSeconds":  24967,  "distanceValueDescription":  "404 km",  "distanceTimeDescription":  "6 horas 56 minutos"  },  {  "originAddress":  "Belo Horizonte, MG, Brasil",  "originLocation":  {  "address":  "Belo Horizonte, MG, Brasil",  "lat":  -19.919052,  "lng":  -43.9386685  },  "destinationAddress":  "Salvador - BA, Brasil",  "destinationLocation":  {  "address":  "Salvador, BA, Brasil",  "lat":  -12.9777378,  "lng":  -38.5016363  },  "distanceValue":  1400284,  "distanceTimeSeconds":  73819,  "distanceValueDescription":  "1.400 km",  "distanceTimeDescription":  "20 horas 30 minutos"  }  ]
~~~

