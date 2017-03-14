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
