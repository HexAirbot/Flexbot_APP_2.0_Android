package com.lewei.lib;

public interface OnTcpListener
{
	void TcpConnected();
	
	void TcpDisconnected();

	void TcpReceive(byte[] data, int size);
}
