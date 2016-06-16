#include <EEPROM.h>

int Rs= 5;
int Gs= 6;
int Bs= 3;
int R = 11;
int G = 10;
int B = 9;
//variáveis de leitura da EEPROM
int l1_en=0;
int l1_r=0;
int l1_g=0;
int l1_b=0;

int l2_en=0;
unsigned int l2_r=0;
unsigned int l2_g=0;
unsigned int l2_b=0;

int targetR=255;
int targetB=255;
int targetG=255;

int spd=0;
int testSequence=0;
int randomMode=0;
unsigned int counter=0;

String report="";
String dataIn="";
String dataOut="";


void setup() {
 Serial.begin(115200);

 
 pinMode(Rs,OUTPUT);
 pinMode(Gs,OUTPUT);
 pinMode(Bs,OUTPUT);
 pinMode(R,OUTPUT);
 pinMode(G,OUTPUT);
 pinMode(B,OUTPUT);
  



printReport();
if(testSequence==1){initialTest();}
randomSeed(analogRead(A2));
}

void printReport(){
  //leitura de eeprom para variáveis
l1_en=EEPROM.read(0);
l1_r=EEPROM.read(1);
l1_g=EEPROM.read(2);
l1_b=EEPROM.read(3);

l2_en=EEPROM.read(4);
l2_r=EEPROM.read(5);
l2_g=EEPROM.read(6);
l2_b=EEPROM.read(7);

//smoothOn=EEPROM.read(8);
spd=EEPROM.read(9);
testSequence=EEPROM.read(10);
randomMode=EEPROM.read(11);  
report="";
report+="(reportStatus)[led1:en=";
report+=l1_en;
report+=",r=";
report+=l1_r;
report+=",g=";
report+=l1_g;
report+=",b=";
report+=l1_b;
report+="]";

report+="[led2:en=";
report+=l2_en;
report+=",r=";
report+=l2_r;
report+=",g=";
report+=l2_g;
report+=",b=";
report+=l2_b;
report+="]";

report+="[other:speed=";
report+=spd;
report+=",test=";
report+=testSequence;
report+=",random=";
report+randomMode;
report+="]";

Serial.println(report);
}

void saveConfig(){
  EEPROM.write(0,l1_en);
  EEPROM.write(1,l1_r);
  EEPROM.write(2,l1_g);
  EEPROM.write(3,l1_b);

  EEPROM.write(4,l2_en);
  EEPROM.write(5,l2_r);
  EEPROM.write(6,l2_g);
  EEPROM.write(7,l2_b);

//  EEPROM.write(8,smoothOn);
  EEPROM.write(9,spd);
  EEPROM.write(10,testSequence);
  EEPROM.write(11,randomMode);

  Serial.println("Settings saved to EEPROM!");
  }

void initialTest(){
  int t=500;
  digitalWrite(R,LOW);
  digitalWrite(G,LOW);
  digitalWrite(B,LOW);

  digitalWrite(R,HIGH);
  digitalWrite(G,LOW);
  digitalWrite(B,LOW);
  delay(t);

  digitalWrite(R,LOW);
  digitalWrite(G,HIGH);
  digitalWrite(B,LOW);
  delay(t);

  digitalWrite(R,LOW);
  digitalWrite(G,LOW);
  digitalWrite(B,HIGH);
  delay(t);

  digitalWrite(R,HIGH);
  digitalWrite(G,HIGH);
  digitalWrite(B,LOW);
  delay(t);

  digitalWrite(R,HIGH);
  digitalWrite(G,LOW);
  digitalWrite(B,HIGH);
  delay(t);

  digitalWrite(R,LOW);
  digitalWrite(G,HIGH);
  digitalWrite(B,HIGH);
  delay(t);

  digitalWrite(R,HIGH);
  digitalWrite(G,HIGH);
  digitalWrite(B,HIGH);
  delay(t);

  digitalWrite(R,LOW);
  digitalWrite(G,LOW);
  digitalWrite(B,LOW);

//Teste na fita led

  digitalWrite(Rs,LOW);
  digitalWrite(Gs,LOW);
  digitalWrite(Bs,LOW);

  digitalWrite(Rs,HIGH);
  digitalWrite(Gs,LOW);
  digitalWrite(Bs,LOW);
  delay(t);

  digitalWrite(Rs,LOW);
  digitalWrite(Gs,HIGH);
  digitalWrite(Bs,LOW);
  delay(t);

  digitalWrite(Rs,LOW);
  digitalWrite(Gs,LOW);
  digitalWrite(Bs,HIGH);
  delay(t);

  digitalWrite(Rs,HIGH);
  digitalWrite(Gs,HIGH);
  digitalWrite(Bs,LOW);
  delay(t);

  digitalWrite(Rs,HIGH);
  digitalWrite(Gs,LOW);
  digitalWrite(Bs,HIGH);
  delay(t);

  digitalWrite(Rs,LOW);
  digitalWrite(Gs,HIGH);
  digitalWrite(Bs,HIGH);
  delay(t);

  digitalWrite(Rs,HIGH);
  digitalWrite(Gs,HIGH);
  digitalWrite(Bs,HIGH);
  delay(t);

  digitalWrite(Rs,LOW);
  digitalWrite(Gs,LOW);
  digitalWrite(Bs,LOW);

  int ad=10;
  for(int i=0;i<255;i++){
    analogWrite(Rs,i);
    delay(ad);
    }
    for(int i=0;i<255;i++){
    analogWrite(Gs,i);
    delay(ad);
    }
    for(int i=0;i<255;i++){
    analogWrite(Bs,i);
    delay(ad);
    }
  
    
  }
  


 void listenToPort(){
  if(Serial.available()){
  dataIn=Serial.readString();
  if(dataIn=="PING?"){
    Serial.println("PONG!");
    }
  if(dataIn.indexOf('@')==4){
    l1_en=dataIn.substring(dataIn.indexOf("l1en@")+5,dataIn.indexOf("@l1")).toInt();
    l2_en=dataIn.substring(dataIn.indexOf("l2en@")+5,dataIn.indexOf("@l2")).toInt();
     
    l1_r=dataIn.substring(dataIn.indexOf("r@")+2,dataIn.indexOf("@r")).toInt();
    l1_g=dataIn.substring(dataIn.indexOf("g@")+2,dataIn.indexOf("@g")).toInt();
    l1_b=dataIn.substring(dataIn.indexOf("b@")+2,dataIn.indexOf("@b")).toInt();
       
    l2_r=dataIn.substring(dataIn.indexOf("R@")+2,dataIn.indexOf("@R")).toInt();
    l2_g=dataIn.substring(dataIn.indexOf("G@")+2,dataIn.indexOf("@G")).toInt();
    l2_b=dataIn.substring(dataIn.indexOf("B@")+2,dataIn.indexOf("@B")).toInt();
   
    
    spd=dataIn.substring(dataIn.indexOf("sp@")+3,dataIn.indexOf("@sp")).toInt();
    testSequence=dataIn.substring(dataIn.indexOf("ts@")+3,dataIn.indexOf("@ts")).toInt();
    randomMode=dataIn.substring(dataIn.indexOf("rdm@")+4,dataIn.indexOf("@rdm")).toInt();
    Serial.println("Config received!"); 
    if(randomMode==1)randomBegin();
    }
    if(dataIn=="SAVE"){
      saveConfig();
      }
    if(dataIn=="TEST"){
      initialTest();
      }
    if(dataIn=="REPORT"){
      printReport();
      }
  }
  }


void randomBegin(){
      targetR=random(0,255);
    targetG=random(0,255);
    targetB=random(0,255);
    Serial.println("Generating random...");
  }


  void loop() {
listenToPort();

if(randomMode==1){

 if(targetR==l2_r&&targetG==l2_g&&targetB==l2_b){
randomBegin();
    }
  
  if(counter>=spd){
 
      if(targetR>l2_r){
      l2_r++;
      }
      if(targetR<l2_r){
      l2_r--;
      }

      if(targetG>l2_g){
      l2_g++;
      }
      if(targetG<l2_g){
      l2_g--;
      }

      if(targetB>l2_b){
      l2_b++;
      }
      if(targetB<l2_b){
      l2_b--;
      }
      counter=0;
  }
    
  }

 

if(l1_en==0){
  digitalWrite(R,LOW);
  digitalWrite(G,LOW);
  digitalWrite(B,LOW);
  }
  
if(l1_en==1){
 analogWrite(R,l1_r);
 analogWrite(G,l1_g);
 analogWrite(B,l1_b);
 }


 if(l2_en==0){
  digitalWrite(Rs,LOW);
  digitalWrite(Gs,LOW);
  digitalWrite(Bs,LOW);
  }
  
if(l2_en==1){
 analogWrite(Rs,l2_r);
 analogWrite(Gs,l2_g);
 analogWrite(Bs,l2_b);
 }

counter++;
delay(1); 
 }
