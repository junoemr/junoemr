Openshift and Kubernetes
========================

Scripts in this directory help release and run Juno in container environments.

build-openshift-image.sh
------------------------

Builds Juno, creates an image and sends that image to the Openshift image registry.

```bash
./build-openshift-image.sh -i <image name> -t <tag name>
```

Example:
```bash
./build-openshift-image.sh -i juno-spring-boot -t juno_spring_boot_1900-00-00
```

Other options:
```
-e - Environment.  Defaults to "osdev", can also be "prod".
-s - Skip build.  Won't build Juno with maven.  Requires that Juno has already been built.
```

run-local-image.sh
------------------

Runs an image locally.

Prerequisite: successfully run build-openshift-image.sh

```bash
./run-local-image.sh -i <image name> -t <tag name>
```
Example of Standard use:
```bash
./run-local-image.sh -i juno-spring-boot -t juno_spring_boot_1900-00-00
```

Other options:
```
-b - Run build.  Will run build-openshift-image.sh automatically.
```

transfer-image-to-github.sh
---------------------------

Copies an image from an openshift image stream to the packages section of the CloudMD-SSI GitHub repository.

Prerequisites: 
* Provide a GitHub Personal Access Token with package write permission to CloudMD-SSI repository.  The token is expected to be in the `/root/.ghcr.io_token`file and is associated with the github username provided to the command.
* successfully run build-openshift-image.sh or otherwise have an image available in the openshift image stream.

Common usage.  Run these two commands to copy the images required for booting Juno in Kubernetes:
```bash
./transfer_image_to_github.sh -e osdev -p juno-build -i juno-spring-boot -t juno_spring_boot_2022-08-08.0 -u <github username>
./transfer_image_to_github.sh -e osdev -p juno-images -i juno-util -t v1.0.1 -u <github username>
```

skaffold.yaml
-------------

Uses the compiled code to run Juno in dev mode a Kubernetes cluster.

**Note:** This is experimental and is largely untested. :firecracker:

Prerequisites:
* A juno war file (i.e. Juno has been built by Maven) 
* A local kubernetes cluster configured to run Fusion
* A sense of adventure :dragon:

Example:
```bash
skaffold run
```

Juno login details:
* Login URL: https://juno.cpdev.ca/juno/index.jsp
* Username: `oscar_host`
* Password: `admin`
* Pin: `1234`
