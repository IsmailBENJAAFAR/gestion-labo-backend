from my_api.serializers.serializer import AnalyseSerializer
from rest_framework import status


def create_analyse(method, data):
    if method == "POST":
        analyse_serializer = AnalyseSerializer(data=data)
        if analyse_serializer.is_valid():
            analyse_serializer.save()
            return {"response_data": {}, "response_status": status.HTTP_200_OK}
        else:
            return {"response_data": {}, "response_status": status.HTTP_400_BAD_REQUEST}
