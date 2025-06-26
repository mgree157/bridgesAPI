import pandas as pd
from sklearn.preprocessing import normalize
# Load CSV
df = pd.read_csv("src/normalizedData.csv")

# Convert to numpy array (optional)
vectors = df.values  # shape = (num_vectors, num_dimensions)
# drop first column
vectors = vectors[:, 1:]
normalized = normalize(vectors)
similarity_matrix = normalized @ normalized.T  # Same as cosine_similarity