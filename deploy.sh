#!/usr/bin/env bash

source .env

setup_credentials() {
	docker login --username "$CONTAINER_REGISTRY_USER" --password "$CONTAINER_REGISTRY_TOKEN" "$CONTAINER_REGISTRY" || log_fatal "error docker login"
	kubectl create secret generic db-secrets --from-env-file=./gestion-exam-resultat/.env || echo "warning: db credentials may have been set already. Proceeding.."
	kubectl create secret generic regcred --from-file=.dockerconfigjson="$HOME"/.docker/config.json --type=kubernetes.io/dockerconfigjson || echo "warning: registry credentials may have been set already. Proceeding.."
}

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

deploy_api_gateway() {
	kubectl apply -f ./api_gateway/gateway_deployment.yaml || deploy_fail "api gateway"
	kubectl apply -f ./api_gateway/gateway_service.yaml || service_fail "api gateway"
}

build_api_gateway_image() {
	cd ./api_gateway/ || return
	rm -rf frontend
	python3 -m venv .venv
	source .venv/bin/activate
	pip install -r ./requirements.txt
	python3 action.py --token "$FRONTEND_ACCESS_TOKEN" --user "$FRONTEND_ACCESS_USER" --registry "$CONTAINER_REGISTRY" --registry-user "$CONTAINER_REGISTRY_USER" --registry-token "$CONTAINER_REGISTRY_TOKEN"
}

if [ -v LOCAL ]; then
	setup_credentials
	(build_api_gateway_image) || log_fatal "couldn't build api gateway image"
fi

(deploy_api_gateway)
(deploy_gestion_examen_service)
