/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conexao;

import framework.Log;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Optional;

/**
 *
 * @author Z D K
 */
public class NetUtils {

    private NetUtils() {

    }

    /**
     * Retorna true se 'a' for IPv4 válido para roteamento (não loopback, não
     * link-local).
     */
    public static boolean isIpv4Roteavel(InetAddress a) {
        if (a == null) {
            return false;
        }
        if (!(a instanceof Inet4Address)) {
            return false;
        }
        return !(a.isAnyLocalAddress()
                || a.isLoopbackAddress()
                || a.isLinkLocalAddress());
    }

    /**
     * Pergunta ao SO qual IP local ele usaria para sair (rota padrão), via UDP
     * + connect().
     */
    public static Optional<InetAddress> descobrirIpRoteadoIPv4() {
        try (DatagramSocket ds = new DatagramSocket()) {
            // cloudflare DNS (1.1.1.1) porta 53 — não enviaremos nada; apenas forçamos o SO a escolher a rota.
            ds.connect(new InetSocketAddress("1.1.1.1", 53));
            InetAddress local = ds.getLocalAddress();
            return isIpv4Roteavel(local) ? Optional.of(local) : Optional.empty();
        } catch (SocketException e) {
            return Optional.empty();
        }
    }

    /**
     * Mapeia um IP local para sua interface de rede (se estiver "up").
     */
    public static Optional<NetworkInterface> descobrirInterfacePor(InetAddress ip) {
        if (ip == null) {
            return Optional.empty();
        }
        try {
            NetworkInterface ni = NetworkInterface.getByInetAddress(ip);
            if (ni == null || !ni.isUp()) {
                return Optional.empty();
            }
            return Optional.of(ni);
        } catch (SocketException e) {
            return Optional.empty();
        }
    }

    /**
     * Caso não exista rota externa, tenta achar um bom IPv4 local varrendo
     * interfaces.
     */
    public static Optional<InetAddress> fallbackIpLocal() {
        try {
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            if (ifaces == null) {
                return Optional.empty();
            }

            for (NetworkInterface ni : Collections.list(ifaces)) {
                try {
                    if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) {
                        continue;
                    }
                } catch (SocketException e) {
                    continue;
                }
                for (InetAddress a : Collections.list(ni.getInetAddresses())) {
                    if (isIpv4Roteavel(a)) {
                        return Optional.of(a);
                    }
                }
            }
            return Optional.empty();
        } catch (SocketException e) {
            return Optional.empty();
        }
    }

    /**
     * Formata MAC (byte[]) como "AA:BB:CC:DD:EE:FF". Retorna null se mac==null.
     */
    public static String formatarMac(byte[] mac) {
        if (mac == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(mac.length * 3 - 1);
        for (int i = 0; i < mac.length; i++) {
            if (i > 0) {
                sb.append(':');
            }
            sb.append(String.format("%02X", mac[i]));
        }
        return sb.toString();
    }

    /**
     * Orquestra tudo (modo estrito por padrão: sem internet => vazio).
     */
    public static Optional<ConexaoAtiva> infoConexaoAtual() {
        return infoConexaoAtual(false); // <— não permitir fallback
    }

    /**
     * Verifica rota externa abrindo TCP em DNS públicos com timeout curto.
     */
    public static boolean temRotaExterna(int timeoutMs) {
        InetSocketAddress[] alvos = new InetSocketAddress[]{
            new InetSocketAddress("1.1.1.1", 53), // Cloudflare
            new InetSocketAddress("8.8.8.8", 53) // Google
        };
        for (InetSocketAddress alvo : alvos) {
            try (Socket s = new Socket()) {
                s.connect(alvo, timeoutMs);
                return true; // conectou a pelo menos um -> há rota
            } catch (Exception ignore) {
                // tenta o próximo
            }
        }
        return false;
    }

    /**
     * Orquestra tudo com opção de fallback local.
     *
     * @param permitirFallback true para aceitar IP local mesmo sem internet.
     */
    public static Optional<ConexaoAtiva> infoConexaoAtual(boolean permitirFallback) {
        Optional<InetAddress> ip = Optional.empty();
        boolean viaFallback = false;

        // 1) Só tenta rota real se houver internet
        if (temRotaExterna(1200)) {
            ip = descobrirIpRoteadoIPv4();
        }

        // 2) Opcionalmente tenta IP local “ok” quando não há internet
        if (ip.isEmpty() && permitirFallback) {
            ip = fallbackIpLocal();
            viaFallback = ip.isPresent();
        }

        if (ip.isEmpty()) {
            return Optional.empty();
        }

        Optional<NetworkInterface> nif = descobrirInterfacePor(ip.get());
        String nome = nif.map(x -> {
            String dn = x.getDisplayName();
            return (dn != null && !dn.isBlank()) ? dn : x.getName();
        }).orElse("desconhecida");

        String mac = nif.map(x -> {
            try {
                return formatarMac(x.getHardwareAddress());
            } catch (SocketException e) {
                return null;
            }
        }).orElse(null);

        boolean ipv6 = ip.get() instanceof Inet6Address;
        return Optional.of(new ConexaoAtiva(ip.get(), nome, mac, ipv6, viaFallback));
    }

}
