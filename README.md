# sms routing

distributed systems project - 2025  
simulates sms delivery in a mobile network using rabbitmq

---

## overview

- users are connected to antennas
- antennas are distributed nodes
- users can move between zones
- messages are routed across antennas
- system ensures delivery even when users move

---

## components

- **directory service**: tracks current antenna for each user

- **antenna node**: manages a zone, handles users and messages

- **user**: connects to an antenna, can move, can send/receive messages

---

## tech stack

- java
- rabbitmq

---

## how to run

1. start rabbitmq
2. run the directory service
3. run one or more antenna nodes
4. simulate users and message sending

---

## notes

built as part of the distributed systems course  
Université Grenoble Alpes - M1 INFO, 2025
