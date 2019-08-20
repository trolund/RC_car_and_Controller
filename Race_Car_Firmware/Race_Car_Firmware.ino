/*
   Firmware for the bluetooh RC-car.
   v: 0.3
   Troels Lund - trolund@gmail.com
*/

#include <Servo.h>
#include<string.h>
#include<stdio.h>
#include <time.h>
#include <stdlib.h>

#define LENGTH 4

int tx = 1;
int rx = 0;
char inSerial[LENGTH];
int turnValue = 50;
int speedValue = 0;

int conLostCount = 0;

Servo myservo;  // create servo object to control a servo
Servo esc;

void Check_Protocol(char);

void setup()
{
  Serial.begin(9600);
  pinMode(tx, OUTPUT);
  pinMode(rx, INPUT);

  srand(time(NULL));

  myservo.attach(8);  // attaches the servo on pin 8 to the servo object
  //esc.attach(9); //Specify the esc signal pin,Here as D9
  esc.attach(9,1000,2000);

  esc.writeMicroseconds(1000); //initialize the signal to 1000
  myservo.write(100);

  Serial.println("Device is ready");
}
void loop()
{

  if (conLostCount > 2000) {
    speedValue = 0;
    turnValue = 50;
    ControlUpdate();
    conLostCount = 0;
    Serial.println("Connection lost! STOP!!!!");
  }

  int i = 0;
  delay(100);
  if (Serial.available() > 0) {
    while (Serial.available() > 0) {
      inSerial[i] = Serial.read();
      i++;
    }
    inSerial[i] = '\0';
    Check_Protocol(inSerial);
    ControlUpdate();
    conLostCount = 0;
  } else {
    conLostCount = conLostCount + 1;
  }
}

void Check_Protocol(char inStr[]) {
 // Serial.print("Data resived:");
 // Serial.println(inStr);

  if (!strcmp(inStr, "U")) {

    if (speedValue >= 0 && speedValue < 175) {
      speedValue = speedValue + 5;
      Serial.println("Speedind up!");
      Serial.println(speedValue);
    } else {
      Serial.println("Max speed is reached.");
    }

  }
  else if (!strcmp(inStr, "D")) {

    if (speedValue <= 175 && speedValue > 0) {
      speedValue = speedValue - 5;
       Serial.println("Speedind down!");
       Serial.println(speedValue);
    } else {
      Serial.println("Car is in total stop.");
    }

  }
  else if (!strcmp(inStr, "L")) {

    if (turnValue >= 0 && turnValue < 115) {
      turnValue = turnValue  + 5;
      Serial.println("going left");
      Serial.println(turnValue);
    }

  }
  else if (!strcmp(inStr, "R")) {

    if (turnValue <= 115 && turnValue > 0) {
      turnValue = turnValue - 5;
      Serial.println("going right");
      Serial.println(turnValue);
    }

  }
  else {
    Serial.println("Fail to follow protocol!");
    return;
  }

}

void ControlUpdate() {
  myservo.write(turnValue);
  esc.write(speedValue);
}

/*
  char delim[] = ",";

  char *ptr = strtok(inStr, delim);

  turnValue = inStr[0] - '0';
  speedValue = inStr[1] - '0';

  Serial.println(map(turnValue, 0, 100, 50, 120) + "," + map(speedValue, 0, 100, 22, 254));

  myservo.write(map(turnValue, 0, 100, 50, 120));
  esc.writeMicroseconds(map(speedValue, 0, 100, 22, 254));

  clearBufferArray(ptr);
  }


  void clearBufferArray(char * pointer)
  {
  for (int i = 0; i < LENGTH; i++)
  {
      inSerial[i] = NULL;
  }
  while(pointer != NULL){
    pointer = NULL;
   pointer++;
  }
  }
*/
