from my_api.models.Analyse import Analyse
from rest_framework import status


def delete_analyse(id: int) -> dict[str, any]:
    try:
        analyse = Analyse.objects.get(id=id)
    except:
        return {"response_data": {}, "response_status": status.HTTP_404_NOT_FOUND}

    analyse.delete()
    return {"response_data": {}, "response_status": status.HTTP_204_NO_CONTENT}
