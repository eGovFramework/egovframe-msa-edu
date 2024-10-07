# Kubernetes에서 서비스 배포

Kubernetes에서 서비스는 두 가지 구성 요소로 이루어져 있습니다.

1. 환경 (Environments)
2. 애플리케이션 (Applications)

그리고 애플리케이션에서 사용할 스토리지를 선택할 수 있으며, NFS 또는 Openstack이 제공하는 Cinder를 사용할 수 있습니다(이 경우 [PaaS-TA](http://paas-ta.kr)를 사용).

# 사전 준비 {#prerequisites}

## kustomize 설치 {#install-kustomize}

kubectl 1.14 이상을 사용하고 있다면, kustomize가 내장되어 있으므로 이 섹션을 무시해도 됩니다.
대신 클러스터에서 최신 버전의 [kubectl](https://kubectl.docs.kubernetes.io/installation/kubectl/)을 설치하는 것을 권장합니다.
그럼에도 불구하고 kustomize를 사용하고 싶다면, [공식 kustomize 문서](https://kustomize.io/)를 참고하세요.

# 서비스 배포 {#deploy-service}

배포 순서를 따라야 합니다.

## 환경 배포 {#deploy-environments}

```sh
$ kustomize build k8s/environments | kubectl apply -f -
```
혹은 kubectl만 사용하는 경우,

```sh
$ kubectl apply -k k8s/environments
```

## 애플리케이션 배포 {#deploy-applications}

NFS를 메인 스토리지로 사용하려면,

```sh
$ kustomize build k8s/stoage/nfs | kubectl apply -f -
```

혹은

```sh
$ kubectl apply -k k8s/stoage/nfs
```

Openstack의 스토리지(CINDER)를 메인 스토리지로 사용하는 경우,

```sh
$ kustomize build k8s/stoage/openstack | kubectl apply -f -
```

혹은

```sh
$ kubectl apply -k k8s/stoage/openstack
```

## 서비스 종료 {#shutdown-service}

단순히 배포 순서를 반대로 적용하면 됩니다.

NFS를 사용하는 경우,

```sh
$ kustomize build k8s/environments | kubectl delete -f -
$ kustomize build k8s/stoage/nfs | kubectl delete -f -  --wait
```

혹은

```sh
$ kubectl delete -k k8s/environments
$ kubectl delete -k k8s/stoage/nfs --wait
```

Openstack의 경우도 비슷하므로 생략합니다.
