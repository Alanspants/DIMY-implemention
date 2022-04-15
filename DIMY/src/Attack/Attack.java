package Attack;

import Attack.AttackUDP.AttackUDPReceive;

public class Attack {

    public static void main(String[] args) {

        /*
        1. Replay attack
        具体描述：attacker 读取到用户发出去的shamir msg，对相应的share进行修改，再一次进行发送
        影响：另一个node无法判断收到的shamir msg是正常node还是attack node发来的
        优化：PGP？
         */
        AttackUDPReceive attackUDPReceive = new AttackUDPReceive();
        attackUDPReceive.start();

        /*
        2. Replay attack
        具体描述：attacker 读取到client node发出去的CBF，将其修改为QBF并再次发送，制造混乱局面
                导致本来的非阳性病人被视为阳性，远远扩大范围
        影响：server没法判断当前发来的BF本质上是CBF还是QBF，只能通过发送BF之前的TCP message获取
        优化：PGP？

        3. Man in middle attack
         */

    }
}
