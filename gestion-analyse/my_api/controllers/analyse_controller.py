from rest_framework import status
from rest_framework.response import Response
from rest_framework.decorators import api_view
from my_api.models.Analyse import Analyse
from my_api.serializers.serializer import AnalyseSerializer
from my_api.services.create import create_analyse
from my_api.services import get
from my_api.services.delete import delete_analyse
from my_api.services.update import update_analyse

# This is more of a django limitation if I wanna keep the api routing the same across microservices


@api_view(["GET", "POST"])
def get_all_and_create(request):
    if request.method == "GET":
        response = get.get_all(method=request.method)
        return Response(response["response_data"], response["response_status"])

    elif request.method == "POST":
        response = create_analyse(method=request.method, data=request.data)
        return Response(response["response_status"])


@api_view(["GET", "PATCH", "DELETE"])
def get_update_delete_by_id(request, id):
    if request.method == "GET":
        response = get.get_by_id(method=request.method, id=id)
        return Response(response["response_data"], response["response_status"])

    elif request.method == "PATCH":
        response = update_analyse(method=request.method, data=request.data, id=id)
        return Response(response["response_status"])

    elif request.method == "DELETE":
        response = delete_analyse(method=request.method, id=id)
        return Response(response["response_status"])
