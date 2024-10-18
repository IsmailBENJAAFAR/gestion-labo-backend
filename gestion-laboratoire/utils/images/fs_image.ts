import fs, { readFileSync } from "fs";

function getByteArray(filePath: string): Buffer {
  // let fileData = fs.readFileSync(filePath).toString("hex");
  let reader;
  try {
    reader = readFileSync(filePath);
    return reader;
  } catch (error) {
    console.log("couldn't read file");
    return Buffer.of();
  }
}

// console.log(getByteArray("./utils/images/AMI.jpeg")[0]);

export default getByteArray;
