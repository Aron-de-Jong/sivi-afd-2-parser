# Sivi AFD 2.0 parser

The goal of this parser is to easily create example JSON files for every entity type. 
The search functionality at https://www.sivi.org/standaarden/sivi-all-finance-standaard/afd-online-2-0/ is not really developer friendly.

Luckily, Sivi also provides the data catalog and codelist as JSON files. 
But those files are quite big. 
So therefore, the motivation of this project is to split up the original JSONs into smaller JSONs.

## Source 
Links to the original JSONs:

- Data catalog (Entity, EntityTypes, Attributes, DataType):
  - https://www.sivi.org/afd-online/assets/datacat.json
- CodeList:
  - https://www.sivi.org/afd-online/assets/codelist.json

Put these two files into folder:
`src/main/resources/sivi`

## Instructions
In the current implementation, the parser is done via the means of executing unit tests.

Go to `test/kotlin/com/sivi_parser` folder.
That folder contains two unit tests, which can generate JSON files: 
- `SiviCodeListParser`: this will output every codelist entry as a separate JSON file.
- `SiviDataCatParser`: this will generate example JSONs for every entity and entity type

The output of the generator will be put into the `sivi` folder. 

## Future improvement ideas

- Generate JSON schemas
- Generate Kotlin / TypeScript schema files
- Perhaps not generate code by means of a unit test, but as a command-line script  
