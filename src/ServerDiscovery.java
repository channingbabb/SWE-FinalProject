import java.net.*;
import java.util.*;

public class ServerDiscovery {
    private static final int DISCOVERY_PORT = 8301;
    private static final String DISCOVERY_MESSAGE = "POKER_SERVER_DISCOVERY";
    private List<ServerInfo> discoveredServers = new ArrayList<>();
    
    public static class ServerInfo {
        private String name;
        private String address;
        private int port;
        
        public ServerInfo(String name, String address, int port) {
            this.name = name;
            this.address = address;
            this.port = port;
        }
        
        public String getName() { return name; }
        public String getAddress() { return address; }
        public int getPort() { return port; }
    }
    
    public List<ServerInfo> discoverServers() {
        discoveredServers.clear();
        
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            socket.setSoTimeout(1000);
            
            byte[] sendData = DISCOVERY_MESSAGE.getBytes();
            
            broadcastToNetwork(socket, sendData);
            
            byte[] receiveData = new byte[1024];
            long endTime = System.currentTimeMillis() + 2000;
            
            while (System.currentTimeMillis() < endTime) {
                try {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    socket.receive(receivePacket);
                    
                    String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    String[] parts = response.split("\\|");
                    if (parts.length == 2) {
                        ServerInfo server = new ServerInfo(
                            parts[1],
                            receivePacket.getAddress().getHostAddress(),
                            Integer.parseInt(parts[0])
                        );
                        if (!discoveredServers.contains(server)) {
                            discoveredServers.add(server);
                        }
                    }
                } catch (SocketTimeoutException e) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return discoveredServers;
    }
    
    private void broadcastToNetwork(DatagramSocket socket, byte[] sendData) throws Exception {
        InetAddress subnet = InetAddress.getByName("255.255.255.255");
        DatagramPacket sendPacket = new DatagramPacket(
            sendData, sendData.length, subnet, DISCOVERY_PORT);
        socket.send(sendPacket);
    }
} 