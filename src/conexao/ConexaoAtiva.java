/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conexao;

import framework.Log;
import java.net.InetAddress;

/**
 *
 * @author Z D K
 */
public class ConexaoAtiva {

    private final InetAddress ipLocal;
    private final String nameInterface;
    private final String mac;
    private final boolean ipv6;
    private final boolean viaFallback;

    public ConexaoAtiva(InetAddress ipLocal, String nameInterface, String mac, boolean ipv6, boolean viaFallback) {
        this.ipLocal = ipLocal;
        this.nameInterface = nameInterface;
        this.mac = mac;
        this.ipv6 = ipv6;
        this.viaFallback = viaFallback;
    }
    
    
    public void log_interface(String operador){
        Log.registrarErro_noEx("Usuário: " + operador + " - IP: " + getIpLocal() + " - IPv6: "+ isIpv6() + " - Interface: " + getNameInterface() + " - Via FallBack: " + isViaFallback() + " - MAC: " + getMac());
    }
   

    @Override
    public String toString() {
        return "";
    }

    public InetAddress getIpLocal() {
        return ipLocal;
    }

    public String getNameInterface() {
        return nameInterface;
    }

    public String getMac() {
        return mac;
    }

    public boolean isIpv6() {
        return ipv6;
    }

    public boolean isViaFallback() {
        return viaFallback;
    }

}
