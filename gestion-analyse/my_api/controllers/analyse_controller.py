from rest_framework import status
from rest_framework.response import Response
from rest_framework.decorators import api_view
from my_api.models.Analyse import Analyse
from my_api.serializers.serializer import AnalyseSerializer
from my_api.services.create import create_analyse
from my_api.services import get
from my_api.services.delete import delete_analyse
from my_api.services.update import update_analyse


@api_view(["GET"])
def get_all(request):
    response = get.get_all(method=request.method)
    return Response(response["response_status"])


@api_view(["GET"])
def get_by_id(request, id):
    response = get.get_by_id(method=request.method, id=id)
    return Response(response["response_status"])


@api_view(["POST"])
def create(request):
    response = create_analyse(method=request.method, data=request.data)
    return Response(response["response_status"])


@api_view(["POST"])
def update(request):
    response = update_analyse(method=request.method, data=request.data)
    return Response(response["response_status"])


@api_view(["DELETE"])
def delete(request, id):
    response = delete_analyse(method=request.method, id=id)
    return Response(response["response_status"])
