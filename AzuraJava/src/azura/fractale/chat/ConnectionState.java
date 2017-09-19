package azura.fractale.chat;

public enum ConnectionState {
	Created, listeningUdp, WaitingHelloUdp, UseUdp, UdpFailed, listeningTcp, WaitingHelloTcp, UseTcp,TcpFailed;
}
