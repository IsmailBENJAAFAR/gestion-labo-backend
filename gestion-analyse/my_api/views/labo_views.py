# from rest_framework import generics, status
# from rest_framework.response import Response
# from rest_framework.decorators import api_view
# from my_api.models.Laboratoire import Laboratoire
# from my_api.serializers import LaboratoireSerializer

# # Routes will be functionally defined

# @api_view(['POST'])
# def create(request):
#     if request.method == 'POST':
#         labo_serializer = LaboratoireSerializer(data=request.data)
#         # check if the request data is valid
#         if labo_serializer.is_valid():
#             # tell the serializer to save the data
#             Laboratoire.objects.create()
#             labo_serializer.save()
#             return Response(status=status.HTTP_200_OK)
#         else:
#             return Response(status=status.HTTP_400_BAD_REQUEST)
#     else:
#         return Response(status=status.HTTP_400_BAD_REQUEST)

# # get all
# @api_view(['GET'])
# def get_all(request):
#     if request.method == 'GET':
#         labos = Laboratoire.objects.all()
#         labo_serializer = LaboratoireSerializer(labos,many=True)
#         return Response(labo_serializer.data)
#     else:
#         return Response(status=status.HTTP_400_BAD_REQUEST)

# # get by id
# @api_view(['GET','POST','DELETE'])
# def RUD_by_id(request,id):
#     try:
#         labo = Laboratoire.objects.get(id=id)
#     except:
#         return Response(status=status.HTTP_404_NOT_FOUND)
    
#     if request.method == 'GET':
#         labo_serializer = LaboratoireSerializer(labo)
#         return Response(labo_serializer.data)
    
#     elif request.method == 'POST':
#         labo_serializer = LaboratoireSerializer(labo,data=request.data)
#         # check if the request data is valid
#         if labo_serializer.is_valid():
#             labo_serializer.save()
#             return Response(status=status.HTTP_200_OK)
#         else:
#             return Response(status=status.HTTP_400_BAD_REQUEST)
        
#     elif request.method == 'DELETE':
#         labo.delete()
#         return Response(status=status.HTTP_204_NO_CONTENT)
        
#     else:
#         return Response(status=status.HTTP_400_BAD_REQUEST)