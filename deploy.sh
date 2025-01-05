#!/usr/bin/env bash

source .env

if [ "$1" == "--help" ]; then
	echo "Setups credentials to connect to the contaienr registry"
	echo "Creates secrets for the kubernetes cluster"
	echo "Runs scripts building the microservices and their images and pushes to the registry"
	echo "NOTE: you can skip the building part by passing 'buildless' as first argument"
	echo "Deploys all the microservices using their deplyoment files"
fi

setup_credentials() {
	docker login --username "$CONTAINER_REGISTRY_USER" --password "$CONTAINER_REGISTRY_TOKEN" "$CONTAINER_REGISTRY" || log_fatal "error docker login"
	kubectl create secret generic db-secrets --from-env-file=./gestion-exam-resultat/.env || echo "warning: db credentials may have been set already. Proceeding.."
	kubectl create secret generic regcred --from-file=.dockerconfigjson="$HOME"/.docker/config.json --type=kubernetes.io/dockerconfigjson || echo "warning: registry credentials may have been set already. Proceeding.."
}

if [ "$1" == "buildless" ]; then
	echo "skipping builds.."
	LOCAL=false
	setup_credentials
fi

log_fatal() {
	err=$?
	echo "$1" && exit $err
}

deploy_fail() {
	log_fatal "couldn't apply $1 deployment"
}

service_fail() {
	log_fatal "couldn't apply $1 service"
}

deploy_gestion_examen_service() {
	kubectl apply -f ./gestion-exam-resultat/gestion_exam_deployment.yaml || deploy_fail "examen"
	kubectl apply -f ./gestion-exam-resultat/gestion_exam_service.yaml || service_fail "examen"
}

build_gestion_examen_image() {
	cd ./gestion-exam-resultat/ || return
	cargo build --release
	python3 -m venv .venv
	source .venv/bin/activate
	pip install -r ./requirements.txt
	python3 deploy-image.py --registry "$CONTAINER_REGISTRY" --registry-user "$CONTAINER_REGISTRY_USER" --registry-token "$CONTAINER_REGISTRY_TOKEN"
}

build_api_gateway_image() {
	cd ./api-gateway/ || return
	rm -rf frontend
	python3 -m venv .venv
	source .venv/bin/activate
	pip install -r ./requirements.txt
	python3 action.py --token "$FRONTEND_ACCESS_TOKEN" --user "$FRONTEND_ACCESS_USER" --registry "$CONTAINER_REGISTRY" --registry-user "$CONTAINER_REGISTRY_USER" --registry-token "$CONTAINER_REGISTRY_TOKEN"
}

build_gestion_utilisateur_image() {
	cd ./gestion-utilisateur/ || return
	python3 -m venv .venv
	source .venv/bin/activate
	pip install -r ./requirements.txt
	python3 deploy-image.py --registry "$CONTAINER_REGISTRY" --registry-user "$CONTAINER_REGISTRY_USER" --registry-token "$CONTAINER_REGISTRY_TOKEN"
}

deploy_api_gateway() {
	kubectl apply -f ./api-gateway/gateway_deployment.yaml || deploy_fail "api gateway"
	kubectl apply -f ./api-gateway/gateway_service.yaml || service_fail "api gateway"
}

deploy_gestion_analyse() {
	kubectl apply -f ./gestion-analyse/gestion_analyse_deployment.yaml || deploy_fail "gestion analyse"
	kubectl apply -f ./gestion-analyse/gestion_analyse_service.yaml || service_fail "gestion analyse"
}

deploy_gestion_utilisateur() {
	kubectl apply -f ./gestion-utilisateur/gestion_utilisateur_deployment.yaml || deploy_fail "gestion utilisateur"
	kubectl apply -f ./gestion-utilisateur/gestion_utilisateur_service.yaml || service_fail "gestion utilisateur"
}

deploy_message_queue() {
	kubectl apply -f ./rabbitmq/rabbitmq_deployment.yaml || deploy_fail "rabbitmq"
	kubectl apply -f ./rabbitmq/rabbitmq_service.yaml|| service_fail "rabbitmq"
}

if [ "$LOCAL" == true ]; then
	setup_credentials
	(build_api_gateway_image) || log_fatal "couldn't build api gateway image"
	(build_gestion_examen_image) || log_fatal "couldn't build examen service image"
fi

# (deploy_message_queue)
(deploy_api_gateway)
(deploy_gestion_utilisateur)
# (deploy_gestion_analyse)
(deploy_gestion_examen_service)
