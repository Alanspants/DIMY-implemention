package Attack;

import Attack.AttackUDP.AttackUDPReceive;

public class Attack {

    public static void main(String[] args) {

        AttackUDPReceive attackUDPReceive = new AttackUDPReceive();
        attackUDPReceive.start();

    }
}
