#!/usr/bin/env python3
"""Cliente RCON minimalista. Uso: rcon.py <host> <port> <password> <comando>"""
import socket
import struct
import sys


def send_packet(sock, req_id, ptype, body: bytes):
    data = struct.pack("<ii", req_id, ptype) + body + b"\x00\x00"
    sock.sendall(struct.pack("<i", len(data)) + data)


def recv_packet(sock):
    length = struct.unpack("<i", sock.recv(4))[0]
    resp = b""
    while len(resp) < length:
        chunk = sock.recv(length - len(resp))
        if not chunk:
            break
        resp += chunk
    req_id, ptype = struct.unpack("<ii", resp[:8])
    return req_id, ptype, resp[8:-2].decode("utf-8", errors="replace")


def main():
    host, port, password, command = sys.argv[1], int(sys.argv[2]), sys.argv[3], sys.argv[4]
    s = socket.create_connection((host, port), timeout=10)
    req_id = 1
    send_packet(s, req_id, 3, password.encode())
    rid, ptype, body = recv_packet(s)
    if rid == -1:
        print("AUTH FAILED")
        sys.exit(1)
    send_packet(s, 2, 2, command.encode())
    rid, ptype, body = recv_packet(s)
    print(body)
    s.close()


if __name__ == "__main__":
    main()