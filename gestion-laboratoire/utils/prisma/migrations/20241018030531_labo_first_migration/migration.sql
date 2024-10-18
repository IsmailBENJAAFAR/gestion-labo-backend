-- CreateTable
CREATE TABLE "Laboratoire" (
    "id" SERIAL NOT NULL,
    "nom" VARCHAR(200) NOT NULL,
    "logo" BYTEA[],
    "nrc" INTEGER NOT NULL,
    "active" BOOLEAN NOT NULL,
    "dateActivation" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "Laboratoire_pkey" PRIMARY KEY ("id")
);
