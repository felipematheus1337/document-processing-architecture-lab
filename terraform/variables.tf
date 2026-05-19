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