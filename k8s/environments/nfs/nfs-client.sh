#!/bin/sh

# NFS 클라이언트 패키지를 설치한다.
sudo apt install -y nfs-common

# 마운트할 디렉터리를 만든다.
sudo mkdir /srv/nfs

# 디렉터리를 마운트한다.
sudo mount -t nfs 192.168.56.21:/srv/nfs /srv/nfs

# 디스크를 확인한다.
df -h
