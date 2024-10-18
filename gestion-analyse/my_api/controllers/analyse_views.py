from rest_framework import status
from rest_framework.response import Response
from rest_framework.decorators import api_view
from my_api.models.Analyse import Analyse
from my_api.serializers.serializer import AnalyseSerializer
from my_api.services.create import create_analyse


@api_view(["POST"])
def create(request):
    response = create_analyse(method=request.method, data=request.data)
    return Response(response["response_status"])


@api_view(["GET"])
def get_all(request):
    if request.method == "GET":
        analyses = Analyse.objects.all()
        analyse_serializer = AnalyseSerializer(analyses, many=True)
        return Response(analyse_serializer.data)
    else:
        return Response(status=status.HTTP_400_BAD_REQUEST)


@api_view(["GET", "POST", "DELETE"])
def RUD_by_id(request, id):
    try:
        analyse = Analyse.objects.get(id=id)
    except:
        return Response(status=status.HTTP_404_NOT_FOUND)

    if request.method == "GET":
        analyse_serializer = AnalyseSerializer(analyse)
        return Response(analyse_serializer.data)

    elif request.method == "POST":
        analyse_serializer = AnalyseSerializer(analyse, data=request.data)
        if analyse_serializer.is_valid():
            analyse_serializer.save()
            return Response(status=status.HTTP_200_OK)
        else:
            return Response(status=status.HTTP_400_BAD_REQUEST)

    elif request.method == "DELETE":
        analyse.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)

    else:
        return Response(status=status.HTTP_400_BAD_REQUEST)
