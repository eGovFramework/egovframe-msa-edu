#!/bin/sh

# NFS 서버 패키지를 설치한다.
sudo apt install -y nfs-kernel-server

# 공유 디렉터리를 만든다.
sudo mkdir /srv/nfs

# 공유 디렉터리에 권한을 부여한다.
sudo chown -R nobody:nogroup /srv/nfs
sudo chmod 777 /srv/nfs

# 공유 디렉터리 내보내기를 설정한다.
echo "/srv/nfs *(rw,sync,no_subtree_check)" | sudo tee /etc/exports

# NFS 서버를 재시작하고 상태를 확인한다.
sudo systemctl restart nfs-kernel-server
sudo systemctl status nfs-kernel-server

# NFS 포트를 방화벽에서 허용한다.
sudo iptables -A INPUT -p tcp --dport 2049 -j ACCEPT
sudo iptables -A INPUT -p udp --dport 2049 -j ACCEPT
