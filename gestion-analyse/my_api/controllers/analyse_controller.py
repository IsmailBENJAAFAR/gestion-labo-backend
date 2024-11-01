from rest_framework.response import Response
from my_api.services.create import create_analyse
from my_api.services.get import *
from my_api.services.delete import delete_analyse
from my_api.services.update import update_analyse
from rest_framework.views import APIView


class AnalyseGeneral(APIView):
    def get(self, request):
        response = get_all()
        return Response(response["response_data"], response["response_status"])

    def post(self, request):
        response = create_analyse(data=request.data)
        return Response(response["response_status"])


class AnalyseById(APIView):
    def get(self, request, id):
        response = get_by_id(id=id)
        return Response(response["response_data"], response["response_status"])

    def patch(self, request, id):
        response = update_analyse(data=request.data, id=id)
        return Response(response["response_status"])

    def delete(self, request, id):
        response = delete_analyse(id=id)
        return Response(response["response_status"])
