using UnityEngine;

[CreateAssetMenu(fileName = "ApiConfig", menuName = "RPG/ApiConfig")]
public class ApiConfig : ScriptableObject
{
    [Tooltip("Backend base URL without trailing slash")]
    public string baseUrl = "http://localhost:8080/api";
}
