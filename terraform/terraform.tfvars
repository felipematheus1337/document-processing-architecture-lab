aws_region   = "us-east-1"
project_name = "document-processing-lab"

vpc_cidr = "10.0.0.0/16"

availability_zones = [
  "us-east-1a",
  "us-east-1b"
]

public_subnet_cidrs = [
  "10.0.1.0/24",
  "10.0.2.0/24"
]

private_app_subnet_cidrs = [
  "10.0.11.0/24",
  "10.0.12.0/24"
]

private_db_subnet_cidrs = [
  "10.0.21.0/24",
  "10.0.22.0/24"
]

my_ip_cidr = "200.152.111.59/32"

db_name               = "documentsdb"
db_username           = "postgres"
db_password           = "TroqueEssaSenha123!"
rds_instance_class    = "db.t4g.micro"
rds_allocated_storage = 20

dynamodb_table_name = "document-processing-events"
s3_bucket_name      = "document-processing-lab-exports-poc-lipe-202605"

ec2_instance_type     = "t3.micro"
kafka_instance_type   = "t3.micro"
bastion_instance_type = "t3.micro"

key_pair_name = "document-processing-lab-key"