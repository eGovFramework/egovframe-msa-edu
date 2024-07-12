# Deploy services on Kubernetes

In Kubernetes, the service consists of two components,

1. Environments
2. Applications

And you can choose the storage in the application either NFS or Cinder provided by Openstack(used by [Pasta](http://paas-ta.kr))

# Prerequisites

## Install kustomize

If you are using kubectl 1.14 or later, it embeded kustomize. So please ignore this section.
I recommend that you install the latest version of [kubectl](https://kubectl.docs.kubernetes.io/installation/kubectl/) for your cluster instead of installing kustomize.
Even so, if you want to use kustomize, refer to [official kustomize doc](https://kustomize.io/). 

# Deploy service

You must follow deployment order.

## Deployenvironments

```sh
$ kustomize build k8s/environments | kubectl apply -f -
```
or if you are using the kubectl only,

```sh
$ kubectl apply -k k8s/environments
```

## Deploy applications

If you want to use NFS as a main storage,

```sh
$ kustomize build k8s/stoage/nfs | kubectl apply -f -
```

or

```sh
$ kubectl apply -k k8s/stoage/nfs
```

In case of using Openstack storage(CINDER) as a main storage,

```sh
$ kustomize build k8s/stoage/openstack | kubectl apply -f -
```

or

```sh
$ kubectl apply -k k8s/stoage/openstack
```

# Shutdown service

Simply apply deployment in reverse order.

If you are using nfs, 

```sh
$ kustomize build k8s/environments | kubectl delete -f -
$ kustomize build k8s/stoage/nfs | kubectl delete -f -  --wait
```

or

```sh
$ kubectl delete -k k8s/environments
$ kubectl delete -k k8s/stoage/nfs --wait
```

The case of openstack is similar, so it is omitted.
