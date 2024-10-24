from gestion_analyse import settings
from my_api.models.Analyse import Analyse


class AnalyseDBRouter:

    def db_for_read(self, model, **hints):
        if settings.DATABASES["testxx"]["PORT"] != 0:
            return "testxx"
        else:
            return "default"

    def db_for_write(self, model, **hints):
        if settings.DATABASES["testxx"]["PORT"] != 0:
            return "testxx"
        else:
            return "default"

    def allow_migrate(self, db, app_label, model_name=Analyse, **hints):
        if settings.DATABASES["testxx"]["PORT"] != 0:
            return db == "testxx"
        else:
            return "default"
