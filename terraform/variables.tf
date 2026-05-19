variable "aws_region" {
  description = "AWS region where resources will be created"
  type        = string
}

variable "project_name" {
  description = "Project name used to prefix AWS resources"
  type        = string
}

variable "vpc_cidr" {
  description = "CIDR block for the main VPC"
  type        = string
}

variable "public_subnet_cidrs" {
  description = "CIDR blocks for public subnets"
  type        = list(string)
}

variable "private_app_subnet_cidrs" {
  description = "CIDR blocks for private application subnets"
  type        = list(string)
}

variable "private_db_subnet_cidrs" {
  description = "CIDR blocks for private database subnets"
  type        = list(string)
}

variable "availability_zones" {
  description = "Availability zones used by the project"
  type        = list(string)
}

variable "my_ip_cidr" {
  description = "Your public IP address in CIDR notation for temporary SSH access"
  type        = string
}

variable "db_name" {
  description = "Initial database name for RDS PostgreSQL"
  type        = string
}

variable "db_username" {
  description = "Master username for RDS PostgreSQL"
  type        = string
}

variable "db_password" {
  description = "Master password for RDS PostgreSQL"
  type        = string
  sensitive   = true
}

variable "rds_instance_class" {
  description = "RDS instance class"
  type        = string
}

variable "rds_allocated_storage" {
  description = "Allocated storage for RDS in GB"
  type        = number
}

variable "dynamodb_table_name" {
  description = "DynamoDB table name for processing events"
  type        = string
}

variable "s3_bucket_name" {
  description = "S3 bucket name for exported processing files"
  type        = string
}