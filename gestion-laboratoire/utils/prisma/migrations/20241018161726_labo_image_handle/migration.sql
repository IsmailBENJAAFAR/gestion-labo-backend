/*
  Warnings:

  - Changed the type of `logo` on the `Laboratoire` table. No cast exists, the column would be dropped and recreated, which cannot be done if there is data, since the column is required.

*/
-- AlterTable
ALTER TABLE "Laboratoire" DROP COLUMN "logo",
ADD COLUMN     "logo" BYTEA NOT NULL;
