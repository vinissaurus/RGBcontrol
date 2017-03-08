#include <EEPROM.h>

int Rs= 5;
int Gs= 6;
int Bs= 3;
int R = 11;
int G = 10;
int B = 9;
int button=A0;

//variáveis de leitura da EEPROM
unsigned int l1_en=0;
unsigned int l1_r=0;
unsigned int l1_g=0;
unsigned int l1_b=0;

unsigned int l2_en=0;
unsigned int l2_r=0;
unsigned int l2_g=0;
unsigned int l2_b=0;

int targetRs=255;
int targetBs=255;
int targetGs=255;
int targetR=255;
int targetB=255;
int targetG=255;

unsigned int spd=0;
int testSequence=0;
int randomMode=0;
int colorHoldTime=0;
int smoothOn=0;
unsigned int counter=0;

String report="";
String dataIn="";
String dataOut="";

unsigned int buttonCount=0;
unsigned int buttonState=0;
unsigned int buttonCase=0;

void setup() {
 Serial.begin(115200);

 
 pinMode(Rs,OUTPUT);
 pinMode(Gs,OUTPUT);
 pinMode(Bs,OUTPUT);
 pinMode(R,OUTPUT);
 pinMode(G,OUTPUT);
 pinMode(B,OUTPUT);
 pinMode(button,INPUT);
  


readReport();
if(testSequence==1&&! Serial.available()){initialTest();}

readReport();
refresh();
printReport();
randomSeed(analogRead(A2));
}

void refresh(){//definir a cor dependendo da configuração do smooth
  switch(smoothOn){
    case 0:{setColor();}
    case 1:{smooth();  }
  }
  
  }

void readReport(){
  //leitura de eeprom para variáveis

l1_en=EEPROM.read(0);
targetR=EEPROM.read(1);
targetG=EEPROM.read(2);
targetB=EEPROM.read(3);

l2_en=EEPROM.read(4);
targetRs=EEPROM.read(5);
targetGs=EEPROM.read(6);
targetBs=EEPROM.read(7);


smoothOn=EEPROM.read(8);
spd=EEPROM.read(9);
testSequence=EEPROM.read(10);
randomMode=EEPROM.read(11); 
colorHoldTime=EEPROM.read(12);
  }

void printReport(){

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
report+=randomMode;
report+=",smooth=";
report+=smoothOn;
report+=",colorHoldTime=";
report+=colorHoldTime;
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

  EEPROM.write(8,smoothOn);
  EEPROM.write(9,spd);
  EEPROM.write(10,testSequence);
  EEPROM.write(11,randomMode);
  EEPROM.write(12,colorHoldTime);
  
  Serial.println("Settings saved to EEPROM!");
  }

void initialTest(){
      targetR=0;
      targetG=0;
      targetB=0;
      targetRs=0;
      targetGs=0;
      targetBs=0;
      setColor();
      spd=1;
      int waitTime=300;

      int testCase=0;
    
    while(testCase<2){  
    int testMatrix[8][3]={
      {0,0,0},
      {0,0,1},
      {0,1,0},
      {0,1,1},
      {1,0,0},
      {1,0,1},
      {1,1,0},
      {1,1,1}
      };
    int i,j;
    
    for(i=0;i<8;i++){
      targetR=0;
      targetG=0;
      targetB=0;
      if(testMatrix[i][0]==1)targetR=255;
      if(testMatrix[i][1]==1)targetG=255;
      if(testMatrix[i][2]==1)targetB=255;
      if(testCase==0){setColor();delay(waitTime);}
      if(testCase==1)smooth();
      if(testMatrix[i][0]==1)targetR=0;
      if(testMatrix[i][1]==1)targetG=0;
      if(testMatrix[i][2]==1)targetB=0;
        if(testCase==0){setColor();delay(waitTime);}
      if(testCase==1)smooth();
      }

      for(i=0;i<8;i++){
      targetRs=0;
      targetGs=0;
      targetBs=0;
      if(testMatrix[i][0]==1)targetRs=255;
      if(testMatrix[i][1]==1)targetGs=255;
      if(testMatrix[i][2]==1)targetBs=255;
         if(testCase==0){setColor();delay(waitTime);}
      if(testCase==1)smooth();
      if(testMatrix[i][0]==1)targetRs=0;
      if(testMatrix[i][1]==1)targetGs=0;
      if(testMatrix[i][2]==1)targetBs=0;
        if(testCase==0){setColor();delay(waitTime);}
      if(testCase==1)smooth();
      
      }
      testCase++;
    }//end of while(case<2)
   spd=EEPROM.read(9);   
  }
  


 void listenToPort(){
  if(Serial.available()){
  dataIn=Serial.readString();

      if(dataIn=="SAVE"){
      saveConfig();
      refresh();
      }
    if(dataIn=="TEST"){
      initialTest();
      readReport();
      if(smoothOn==1)smooth();
      if(smoothOn==0)setColor();
      }
    if(dataIn=="REPORT"){
      readReport();
      printReport();
      refresh();
      }
  if(dataIn=="PING?"){
    Serial.println("PONG!");
    }

    if(dataIn.indexOf(':')!=-1){
        l1_en =getValue(dataIn, ':', 0).toInt();
        
      if(l1_en==0){
      targetR=0;
      targetG=0;
      targetB=0;
      }

          if(l1_en==1){ 
    targetR=getValue(dataIn, ':', 1).toInt();
    targetG=getValue(dataIn, ':', 2).toInt();
    targetB=getValue(dataIn, ':', 3).toInt();
    }
    
    l2_en=getValue(dataIn, ':', 4).toInt();
      if(l2_en==0){
    targetRs=0;
    targetGs=0;
    targetBs=0;
    }

    if(l2_en==1){ 
    targetRs=getValue(dataIn, ':', 5).toInt();
    targetGs=getValue(dataIn, ':', 6).toInt();
    targetBs=getValue(dataIn, ':', 7).toInt();
    }

    smoothOn=getValue(dataIn, ':', 8).toInt();
    spd=getValue(dataIn, ':', 9).toInt();
    testSequence=getValue(dataIn, ':', 10).toInt();
    randomMode=getValue(dataIn, ':', 11).toInt();
    colorHoldTime=getValue(dataIn, ':', 12).toInt();
    
    refresh();   //define as configurações recebidas no dispositivo
      }
    

  }
  dataIn="";

  
  }


void randomBegin(){
    targetR=random(0,255);
    targetG=random(0,255);
    targetB=random(0,255);
    //Serial.println("Generating random...");
  }


void setColor(){//método para definir a cor sem suavizar
    l1_r=targetR;
    l1_g=targetG;
    l1_b=targetB;

    l2_r=targetRs;
    l2_g=targetGs;
    l2_b=targetBs;

   analogWrite(R,l1_r);
    analogWrite(G,l1_g);
    analogWrite(B,l1_b);
    analogWrite(Rs,l2_r);
    analogWrite(Gs,l2_g);
    analogWrite(Bs,l2_b);
    
    Serial.println("ready");
  }

void smooth(){//método para definir a cor com suavização
     while(true){
      //for the common leds
      if(l1_r>targetR)l1_r--;
      if(l1_r<targetR)l1_r++;
      
      if(l1_g>targetG)l1_g--;
      if(l1_g<targetG)l1_g++;
      
      if(l1_b>targetB)l1_b--;
      if(l1_b<targetB)l1_b++;

      //for the strip leds
      if(l2_r>targetRs)l2_r--;
      if(l2_r<targetRs)l2_r++;
      
      if(l2_g>targetGs)l2_g--;
      if(l2_g<targetGs)l2_g++;
      
      if(l2_b>targetBs)l2_b--;
      if(l2_b<targetBs)l2_b++;

    analogWrite(R,l1_r);
    analogWrite(G,l1_g);
    analogWrite(B,l1_b);
    analogWrite(Rs,l2_r);
    analogWrite(Gs,l2_g);
    analogWrite(Bs,l2_b);

    delay(spd);

    if(l1_r==targetR&&l1_g==targetG&&l1_b==targetB&&l2_r==targetRs&&l2_g==targetGs&&l2_b==targetBs)break;
      }

      Serial.println("ready");
  }

void buttonRead(){
  buttonState=digitalRead(button);
  
  
  if(buttonState==1){
    buttonCount++;

  if(buttonCount>1500){
    targetR=0;
    targetG=0;
    targetB=0;
    targetRs=0;
    targetGs=0;
    targetBs=0;
 
    smooth();
      
  digitalWrite(R,LOW);
  digitalWrite(G,LOW);
  digitalWrite(B,LOW);
  digitalWrite(Rs,LOW);
  digitalWrite(Gs,LOW);
  digitalWrite(Bs,LOW);
    l1_en=0;
    l2_en=0;
    buttonCount=0;
    delay(200);
      }
    }
    
    
    if(buttonState==0&&buttonCount>10&&buttonCount<1000){
       
    if(l1_en==1){
    targetR=random(0,255);
    targetG=random(0,255);
    targetB=random(0,255);
    targetRs=targetR;
    targetGs=targetG;
    targetBs=targetB;

    smooth();
    
    analogWrite(R,l1_r);
    analogWrite(G,l1_g);
    analogWrite(B,l1_b);
    analogWrite(Rs,l2_r);
    analogWrite(Gs,l2_g);
    analogWrite(Bs,l2_b);
    }

    if(l1_en==0){
   //leitura de eeprom para variáveis
    l1_en=1;
    l2_en=1;

    targetR=EEPROM.read(1);
    targetG=EEPROM.read(2);
    targetB=EEPROM.read(3);
    targetRs=EEPROM.read(5);
    targetGs=EEPROM.read(6);
    targetBs=EEPROM.read(7);
    
    smooth();

    
    analogWrite(R,l1_r);
    analogWrite(G,l1_g);
    analogWrite(B,l1_b);
    analogWrite(Rs,l2_r);
    analogWrite(Gs,l2_g);
    analogWrite(Bs,l2_b);
      }
      buttonCount=0;
    }
    
    }
  
  
int timeToMorph=0;

  void loop() {
buttonRead();
listenToPort();

if(randomMode==1){

 if(targetR==l2_r&&targetG==l2_g&&targetB==l2_b){
randomBegin();

    }

if(timeToMorph==0&&counter==colorHoldTime){
  timeToMorph=1;
  counter=0;
  }  
  
if(timeToMorph==1&&counter==16-spd){
 
      if(targetR>l2_r){
      l2_r++;
      }
      if(targetR<l2_r){
      l2_r--;
      }
      l1_r=255-l2_r;
      
      if(targetG>l2_g){
      l2_g++;
      }
      if(targetG<l2_g){
      l2_g--;
      }
      l1_g=255-l2_g;
      
      if(targetB>l2_b){
      l2_b++;
      }
      if(targetB<l2_b){
      l2_b--;
      }
      l1_b=255-l2_b;
      
      counter=0;
 }
    counter=counter+1;
    
    if(l1_r==targetR&&l1_g==targetG&&l1_b==targetB&&l2_r==targetRs&&l2_g==targetGs&&l2_b==targetBs){//se chegar nas cores desejadas o led para de variar a cor
      timeToMorph=0;
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


delay(1); 
 }



 String getValue(String data, char separator, int index)
{
  int found = 0;
  int strIndex[] = {0, -1};
  int maxIndex = data.length()-1;

  for(int i=0; i<=maxIndex && found<=index; i++){
    if(data.charAt(i)==separator || i==maxIndex){
        found++;
        strIndex[0] = strIndex[1]+1;
        strIndex[1] = (i == maxIndex) ? i+1 : i;
    }
  }

  return found>index ? data.substring(strIndex[0], strIndex[1]) : "";
}
