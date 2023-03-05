package com.diab.subnetcalculator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class NetzwerkManager {
    public static class Subnet {
        public String name;
        public int benötigteGröße;
        public int zugeteileteGröße;
        public String addresse;
        public String maskeCIDR;
        public String decMask;
        public String ersteNutzbareHost;
        public String lezteNutzbareHost;
        public String bereich;
        public String broadcast;

        public Subnet() {

        }

        public Subnet(String netAddress) throws IllegalArgumentException {
            if (!checkIfValidNetworkAddress(netAddress)) {
                throw new IllegalArgumentException("netAddress not valid");
            }
            this.name = "A";
            int currentIp = findeErsteIp(netAddress);
            this.addresse = konventiereIpToQuartett(currentIp);
            this.maskeCIDR = "/" + netAddress.split("/")[1];
            this.decMask = todecmask(Integer.parseInt(netAddress.split("/")[1]));
            this.zugeteileteGröße = findeNutzbareHosts(Integer.parseInt(netAddress.split("/")[1]));
            this.broadcast = konventiereIpToQuartett(currentIp + zugeteileteGröße + 1);
            this.ersteNutzbareHost = konventiereIpToQuartett(currentIp + 1);
            this.lezteNutzbareHost = konventiereIpToQuartett(currentIp + zugeteileteGröße);
            this.bereich = ersteNutzbareHost + " - " + lezteNutzbareHost;


        }
    }

    /**
     * Überprüfen, ob eine IP-Adresse eine gültige Netzwerkadresse ist.
     */
    public static boolean checkIfValidNetworkAddress(String netAddress) {
        String[] ipPart = netAddress.split("/");
        /*iPart[0]  =  A.B.C.D
         * ipPart[1] =  mask
         */
        if (ipPart.length != 2) {
            return false;
        }
        String[] ipOctets = ipPart[0].split("\\.");
        if (ipOctets.length != 4) {
            return false;
        }

        for (String octet : ipOctets) {
            if (Integer.parseInt(octet) < 0 || Integer.parseInt(octet) > 255) {
                return false;
            }
        }
        //return true if the last test is true
        return Integer.parseInt(ipPart[1]) >= 0 && Integer.parseInt(ipPart[1]) <= 32;
    }


    /**
     * Berechne VLSM.
     */
    public static List<Subnet> berechneVLSM(String majorNetwork, Map<String, Integer> subnets) {
        Map<String, Integer> sortedSubnets = sortiereMap(subnets);
        List<Subnet> output = new ArrayList<>();
        int currentIp = findeErsteIp(majorNetwork);

        for (String key : sortedSubnets.keySet()) {  // for all subNets
            Subnet subnet = new Subnet();

            subnet.name = key;
            subnet.addresse = konventiereIpToQuartett(currentIp);

            int neededSize = sortedSubnets.get(key);
            subnet.benötigteGröße = neededSize;

            int mask = berechneMaske(neededSize);
            subnet.maskeCIDR = "/" + mask;
            subnet.decMask = todecmask(mask);

            int allocatedSize = findeNutzbareHosts(mask);
            subnet.zugeteileteGröße = allocatedSize;
            subnet.broadcast = konventiereIpToQuartett(currentIp + allocatedSize + 1);

            String firstUsableHost = konventiereIpToQuartett(currentIp + 1);
            String lastUsableHost = konventiereIpToQuartett(currentIp + allocatedSize);
            subnet.bereich = firstUsableHost + " - " + lastUsableHost;

            output.add(subnet);

            currentIp += allocatedSize + 2;
        }

        return output;
    }

    /**
     * Map nach absteigender Wertereihenfolge sortieren
     */
    private static Map<String, Integer> sortiereMap(Map<String, Integer> map) {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(map.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());  // descending
            }
        });

        Map<String, Integer> sortierteMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : entries) {
            sortierteMap.put(entry.getKey(), entry.getValue());
        }

        return sortierteMap;
    }

    /**
     * Führen das IP-Adressquartett zu einer einzigen binären Zeichenfolge zusammen
     */
    public static int KonvertiereQuartettToBinaryString(String ipAddress) {
        String[] ip = ipAddress.split("[./]");

        int octet1 = Integer.parseInt(ip[0]);
        int octet2 = Integer.parseInt(ip[1]);
        int octet3 = Integer.parseInt(ip[2]);
        int octet4 = Integer.parseInt(ip[3]);

        int output = octet1;
        output = (output << 8) + octet2;
        output = (output << 8) + octet3;
        output = (output << 8) + octet4;

        return output;
    }

    /**
     * Trennt die binäre Zeichenfolge der IP-Adresse in ein Quartett.
     */
    public static String konventiereIpToQuartett(int ipAddress) {
        int octet1 = (ipAddress >> 24) & 255;
        int octet2 = (ipAddress >> 16) & 255;
        int octet3 = (ipAddress >> 8) & 255;
        int octet4 = ipAddress & 255;

        return octet1 + "." + octet2 + "." + octet3 + "." + octet4;
    }

    /**
     * Sucht die erste IP-Adresse für das angegebene Netzwerk.
     */
    public static int findeErsteIp(String majorNetwork) {
        String[] ip = majorNetwork.split("/");
        int maske = Integer.parseInt(ip[1]); // parse CIDR maske
        int offset = Integer.SIZE - maske;

        int majorAddress = KonvertiereQuartettToBinaryString(majorNetwork);
        //return ersteIP
        return  (majorAddress >> offset) << offset;
    }

    /**
     * Maske berechnen und in CIDR-Notation zurückgeben.
     */
    public static int berechneMaske(int benötigteGröße) {
        if(isPowerOfTwo(benötigteGröße +1)){
            benötigteGröße+=1;
        }
        int größteBit = Integer.highestOneBit(benötigteGröße);
        int position = (int) (Math.log(größteBit) / Math.log(2));
        return Integer.SIZE - (position + 1);   // +1 weil Position mit 0 beginnt
    }

    /**
     * Ermittelung von die Gesamtzahl der nutzbaren IP-Adressen/Hosts.
     */
    public static int findeNutzbareHosts(int maske) {
        return (int) Math.pow(2, Integer.SIZE - maske) - 2;
    }

    /**
     * Konvertierung von Maske von der CIDR \slash-Notation in die Ganzzahlmaske
     */
    public static int toIntMaske(int maske) {
        if (maske == 0) {
            return 0;
        }
        int allOne = -1;    // '255.255.255.255'
        return allOne << (Integer.SIZE - maske);
    }

    /**
     * Konvertierung von Maske von der CIDR-Notation in die Quartettform.
     */
    public static String todecmask(int mask) {
        return konventiereIpToQuartett(toIntMaske(mask));
    }

    /**
     * Konvertierung der Maske von der CIDR-Schrägstrichnotation in die WildcardMask-Int-Form.
     **/

    public static int toIntWildcardMaske(int maske) {
        return ~toIntMaske(maske);
    }

    /**
     * Konvertierung der Maske von der CIDR-Schrägstrichnotation in die WildcardMask-Quartettform.
     **/
    public static String toDecWildCardMaske(int mask) {
        return konventiereIpToQuartett(toIntWildcardMaske(mask));
    }

    /**
     * Konvertierung von ip von der int-Slash-Notation in die binäre Quartettform..
     **/
    public static String toBinOctet(int address) {
        int allesOne = -1;
        String binAddress = String.format("%32s", Integer.toBinaryString(address & allesOne)).replace(' ', '0');
        String octet[] = {binAddress.substring(0, 8),
                binAddress.substring(8, 16),
                binAddress.substring(16, 24),
                binAddress.substring(24, 32)};

        return octet[0] + "." + octet[1] + "." + octet[2] + "." + octet[3];
    }


    //private Hilfsmethoden
    private static boolean isPowerOfTwo(int n) {
        return n != 0 && ((n & (n - 1)) == 0);
    }

    //private Konstruktor
    private NetzwerkManager() {

    }

}