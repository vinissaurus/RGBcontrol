#include <EEPROM.h>

int Rs= 6;
int Gs= 5;
int Bs= 3;
int R = 9;
int G = 10;
int B = 11;
//variáveis de leitura da EEPROM
int l1_en=0;
int l1_r=0;
int l1_g=0;
int l1_b=0;

int l2_en=0;
int l2_r=0;
int l2_g=0;
int l2_b=0;

int smoothOn=0;
int spd=0;
int testSequence=0;

String report="";
String dataIn="";
//decode_results results;
//char IRcode[6][4]= {
//  {'F700FF','F7807F','F740BF','F7C03F'},
//  {'F720DF','F7A05F','F7609F','F7E01F'},
//  {'F710EF','F7906F','F750AF','F7D02F'},
//  {'F730CF','F7B04F','F7708F','F7F00F'},
//  {'F708F7','F78877','F748B7','F7C837'},
//  {'F728D7','F7A857','F76897','F7E817'}
//  };

void setup() {
 Serial.begin(9600);

 
 pinMode(Rs,OUTPUT);
 pinMode(Gs,OUTPUT);
 pinMode(Bs,OUTPUT);
 pinMode(R,OUTPUT);
 pinMode(G,OUTPUT);
 pinMode(B,OUTPUT);
  
  //leitura de eeprom para variáveis
l1_en=EEPROM.read(0);
l1_r=EEPROM.read(1);
l1_g=EEPROM.read(2);
l1_b=EEPROM.read(3);

l2_en=EEPROM.read(4);
l2_r=EEPROM.read(5);
l2_g=EEPROM.read(6);
l2_b=EEPROM.read(7);

smoothOn=EEPROM.read(8);
spd=EEPROM.read(9);
testSequence=EEPROM.read(10);

printReport();
}

void printReport(){
report+="[led1:en=";
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

report+="[other:smooth=";
report+=smoothOn;
report+=",speed=";
report+=spd;
report+=",test=";
report+=testSequence;
report+="]";

Serial.println(report);
}

int t=5;
void loop() {
//if(Serial.available()){
  printReport();
  dataIn=Serial.readString();
  if(dataIn=="ei"){
    Serial.println("ou");
    }
  //}


 
 }
